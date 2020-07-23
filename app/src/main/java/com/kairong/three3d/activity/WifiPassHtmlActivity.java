package com.kairong.three3d.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.kairong.esptouch.EsptouchTask;
import com.kairong.esptouch.IEsptouchResult;
import com.kairong.esptouch.IEsptouchTask;
import com.kairong.esptouch.util.ByteUtil;
import com.kairong.esptouch.util.TouchNetUtil;
import com.kairong.three3d.IndexHtmlActivity;
import com.kairong.three3d.R;
import com.kairong.three3d.config.HtmlConfig;
import com.kairong.three3d.config.PrinterConfig;
import com.kairong.three3d.config.WifiConfig;
import com.kairong.three3d.touchv1.EspTouchActivityAbs;
import com.kairong.three3d.touchv1.EspTouchApp;
import com.kairong.three3d.util.PermissionCheckUtil;
import com.kairong.three3d.util.StlDealUtil;
import com.kairong.three3d.util.WebHost;
import com.kairong.three3d.util.WebViewClientUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WifiPassHtmlActivity extends EspTouchActivityAbs {

    private static final String TAG = WifiPassHtmlActivity.class.getSimpleName();

    private EspTouchViewModel mViewModel;

    private EsptouchAsyncTask4 mTask;

    private Context context;

    private ImageButton imageButton;

    private WebHost webHost;
    private String WEB_URL;
    private WebView webView;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_pass);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        // 拿到webView组件
        webView = findViewById(R.id.wifi_pass);
        // 拿到webView的设置对象
        WebSettings settings = webView.getSettings();
        // settings.setAppCacheEnabled(true); // 开启缓存
        settings.setJavaScriptEnabled(true); // 开启javascript支持
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webHost = new WebHost(this, mainHandler);
        //JS映射
        webView.addJavascriptInterface(webHost, "js");

        // 复写WebViewClient类的shouldOverrideUrlLoading方法
        webView.setWebViewClient(WebViewClientUtil.getMyWebViewClient());
        webView.setWebChromeClient(WebViewClientUtil.getGoogleClient());
//        webView.loadUrl(HtmlUtil.BULID_MODULE_URL);
        WebHost.disableLongClick(webView);
        Intent intent = getIntent();
        // 获取到传递参数
        WEB_URL = intent.getStringExtra("url");
        if (WEB_URL == null || WEB_URL.length() == 0) {
            WEB_URL = HtmlConfig.WIFI_PASS_HTML;
        }
        webView.loadUrl(WEB_URL);


        mViewModel = new EspTouchViewModel();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, PermissionCheckUtil.REQUEST_PERMISSION);
        }

        EspTouchApp.getInstance().observeBroadcast(this, broadcast -> {
            Log.d(TAG, "onCreate: Broadcast=" + broadcast);
            onWifiChanged();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String ESP_8266_URL = msg.obj.toString();
                    if (ESP_8266_URL != null && ESP_8266_URL.length() > 0) {
                        webView.loadUrl("javascript:checkPass(" +1 + ")");
                        if (PrinterConfig.ESP_8266_URL == null || PrinterConfig.ESP_8266_URL.length() == 0) {
                            StlDealUtil.savePrinterUrl(context, ESP_8266_URL);
                        } else {
                            StlDealUtil.updatePrinterUrl(context, ESP_8266_URL);
                        }
                        if (PrinterConfig.printer_gcode != null && PrinterConfig.printer_gcode.length() > 0) {
                            Intent it = new Intent(context.getApplicationContext(), PrinterStartActivity.class);
                            context.startActivity(it);
                        } else {
//                            Intent it = new Intent(context.getApplicationContext(), Esp8266Activity.class);
//                            context.startActivity(it);
                        }
                        Intent it = new Intent(context.getApplicationContext(), IndexHtmlActivity.class);
                        context.startActivity(it);
                        finish();
                    }
                    else {
                        webView.loadUrl("javascript:checkPass(" +0 + ")");
                    }
                    break;
                case 66:
                    webView.loadUrl("javascript:wifiName('" + msg.obj.toString() + "')");
                    break;
                case 67:
                    WifiConfig.password = msg.obj.toString().getBytes();
                    executeEsptouch();
                    break;
                case 5:
                    WebViewClientUtil.actionKey(KeyEvent.KEYCODE_BACK);
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionCheckUtil.REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onWifiChanged();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.esptouch1_location_permission_title)
                        .setMessage(R.string.esptouch1_location_permission_message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
                        .show();
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected String getEspTouchVersion() {
        return getString(R.string.esptouch1_about_version, IEsptouchTask.ESPTOUCH_VERSION);
    }

    private StateResult check() {
        StateResult result = checkPermission();
        if (!result.permissionGranted) {
            return result;
        }
        result = checkLocation();
        result.permissionGranted = true;
        if (result.locationRequirement) {
            return result;
        }
        result = checkWifi();
        result.permissionGranted = true;
        result.locationRequirement = false;
        return result;
    }

    private void onWifiChanged() {
        StateResult stateResult = check();
        if(stateResult.ssid!=null&&stateResult.ssid.length()>0) {
            WifiConfig.ssid = stateResult.ssid.getBytes();
            WifiConfig.wifi_name = stateResult.ssid;
        }
       if(stateResult.bssid!=null&&stateResult.bssid.length()>0) {
           WifiConfig.bssid = stateResult.bssid;
        }
        if (stateResult.wifiConnected) {
            if (stateResult.is5G) {
                webView.loadUrl("javascript:checkPass(" +3 + ",'"+getString(R.string.esptouch1_wifi_5g_message)+"')");
            }
        } else {
            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(WifiPassHtmlActivity.this)
                        .setMessage(R.string.esptouch1_configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        }
        if(WifiConfig.wifi_name !=null && WifiConfig.wifi_name.length()>0) {
            webView.loadUrl("javascript:wifiName('" + WifiConfig.wifi_name + "')");
        }
        mViewModel.invalidateAll();
    }

    private void executeEsptouch() {
        /*EspTouchViewModel viewModel = mViewModel;
        byte[] ssid = viewModel.ssidBytes == null ? ByteUtil.getBytesByString(viewModel.ssid)
                : viewModel.ssidBytes;
        byte[] bssid = TouchNetUtil.parseBssid2bytes(viewModel.bssid);
        CharSequence devCountStr = mViewModel.deviceCountEdit.getText();
        byte[] deviceCount = devCountStr == null ? new byte[0] : devCountStr.toString().getBytes();
        byte[] broadcast = {(byte) (mViewModel.packageModeGroup.getCheckedRadioButtonId() == R.id.packageBroadcast
                ? 1 : 0)};*/
        byte[] bssid = TouchNetUtil.parseBssid2bytes(WifiConfig.bssid);
        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(this, mainHandler,webView);
        mTask.execute( WifiConfig.ssid, bssid, WifiConfig.password, "1".getBytes(), "1".getBytes());
    }

    public static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private WeakReference<WifiPassHtmlActivity> mActivity;

        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;
        private WebView webView;
        private Handler handler;

        public EsptouchAsyncTask4(WifiPassHtmlActivity activity, Handler handler,WebView webView) {
            mActivity = new WeakReference<>(activity);
            this.handler = handler;
            this.webView = webView;
        }

        void cancelEsptouch() {
            cancel(true);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mResultDialog != null) {
                mResultDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            Activity activity = mActivity.get();
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(activity.getString(R.string.esptouch1_configuring_message));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(dialog -> {
                synchronized (mLock) {
                    if (mEsptouchTask != null) {
                        mEsptouchTask.interrupt();
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
                    (dialog, which) -> {
                        synchronized (mLock) {
                            if (mEsptouchTask != null) {
                                mEsptouchTask.interrupt();
                            }
                        }
                    });
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {
            Context context = mActivity.get();
            if (context != null) {
                IEsptouchResult result = values[0];
                Log.i(TAG, "EspTouchResult: " + result);
//                String text = result.getBssid() + " is connected to the wifi";
                String text = "已连接到WiFi: "+ WifiConfig.ssid;
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            WifiPassHtmlActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            WifiPassHtmlActivity activity = mActivity.get();
            activity.mTask = null;
            mProgressDialog.dismiss();
            if (result == null) {
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed_port)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
                return;
            }

            // check whether the task is cancelled and no results received
            IEsptouchResult firstResult = result.get(0);
            if (firstResult.isCancelled()) {
                return;
            }
            // the task received some results including cancelled while
            // executing before receiving enough results

            if (!firstResult.isSuc()) {
                webView.loadUrl("javascript:checkPass("+0+")");
                return;
            }

            ArrayList<CharSequence> resultMsgList = new ArrayList<>(result.size());

            String ipAddress = null;
            for (IEsptouchResult touchResult : result) {
                ipAddress = touchResult.getInetAddress().getHostAddress();
                String message = activity.getString(R.string.esptouch1_configure_result_success_item,
                        touchResult.getBssid(), ipAddress);
                resultMsgList.add(message);

            }

            String ESP_8266_URL = null;
            if (ipAddress != null && ipAddress.length() > 0) {
                ESP_8266_URL = "http://" + ipAddress + "/";
            } else {
                ESP_8266_URL = null;
            }

            resultMsgList.add(";ipAddress:" + ESP_8266_URL);
            // 跳转到打印机控制界面
            if (ESP_8266_URL != null && ESP_8266_URL.length() > 0) {
                Message message = new Message();
                message.what = 1;
                message.obj = ESP_8266_URL;
                handler.sendMessage(message);
            }
            /*CharSequence[] items = new CharSequence[resultMsgList.size()];
            mResultDialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.esptouch1_configure_result_success)
                    .setItems(resultMsgList.toArray(items), null)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            mResultDialog.setCanceledOnTouchOutside(false);


            mResultDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            });*/
        }
    }

    class EspTouchViewModel {
        TextView apSsidTV;
        TextView apBssidTV;
        EditText apPasswordEdit;
        EditText deviceCountEdit;
        RadioGroup packageModeGroup;
        TextView messageView;
        ImageButton confirmBtn;

        String ssid;
        byte[] ssidBytes;
        String bssid;

        CharSequence message;

        boolean confirmEnable;

        void invalidateAll() {

            if(bssid!=null&&bssid.length()>0) {
                WifiConfig.bssid = bssid;
            }
           if(ssid!=null&&ssid.length()>0) {
               WifiConfig.ssid = ssid.getBytes();
               Message wifi_message = new Message();
               wifi_message.what = 66;
               wifi_message.obj = ssid;
               mainHandler.sendMessage(wifi_message);
           }
        }
    }

}
