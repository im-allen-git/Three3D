package com.example.three3d.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.example.esptouch.EsptouchTask;
import com.example.esptouch.IEsptouchResult;
import com.example.esptouch.IEsptouchTask;
import com.example.esptouch.util.ByteUtil;
import com.example.esptouch.util.TouchNetUtil;
import com.example.three3d.R;
import com.example.three3d.activity.Esp8266Activity;
import com.example.three3d.touchv1.EspTouchActivityAbs;
import com.example.three3d.touchv1.EspTouchApp;
import com.example.three3d.util.StlUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WifiPassActivity extends EspTouchActivityAbs {

    private static final String TAG = WifiPassActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 0x01;

    private EspTouchViewModel mViewModel;

    private EsptouchAsyncTask4 mTask;

    private Context context;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_wifi_password);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;

        mViewModel = new EspTouchViewModel();
        mViewModel.apSsidTV = findViewById(R.id.apSsidText);
        mViewModel.apBssidTV = findViewById(R.id.apBssidText);
        mViewModel.apPasswordEdit = findViewById(R.id.apPasswordEdit);
        mViewModel.deviceCountEdit = findViewById(R.id.deviceCountEdit);
        mViewModel.packageModeGroup = findViewById(R.id.packageModeGroup);
        mViewModel.messageView = findViewById(R.id.messageView);
        mViewModel.confirmBtn = findViewById(R.id.confirmBtn);
        mViewModel.confirmBtn.setOnClickListener(v -> executeEsptouch());

        mViewModel.apPasswordEdit.setText("ldl@123456789");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, REQUEST_PERMISSION);
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
                        if(StlUtil.ESP_8266_URL == null || StlUtil.ESP_8266_URL.length() == 0){
                            StlUtil.savePrinterUrl(context, ESP_8266_URL);
                        } else{
                            StlUtil.updatePrinterUrl(context, ESP_8266_URL);
                        }
                        Intent it = new Intent(context.getApplicationContext(), Esp8266Activity.class);
                        context.startActivity(it);
                        finish();
                    }
                    break;
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
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
        mViewModel.message = stateResult.message;
        mViewModel.ssid = stateResult.ssid;
        mViewModel.ssidBytes = stateResult.ssidBytes;
        mViewModel.bssid = stateResult.bssid;
        mViewModel.confirmEnable = false;
        if (stateResult.wifiConnected) {
            mViewModel.confirmEnable = true;
            if (stateResult.is5G) {
                mViewModel.message = getString(R.string.esptouch1_wifi_5g_message);
            }
        } else {
            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(WifiPassActivity.this)
                        .setMessage(R.string.esptouch1_configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        }
        mViewModel.invalidateAll();
    }

    private void executeEsptouch() {
        EspTouchViewModel viewModel = mViewModel;
        byte[] ssid = viewModel.ssidBytes == null ? ByteUtil.getBytesByString(viewModel.ssid)
                : viewModel.ssidBytes;
        CharSequence pwdStr = mViewModel.apPasswordEdit.getText();
        byte[] password = pwdStr == null ? null : ByteUtil.getBytesByString(pwdStr.toString());
        byte[] bssid = TouchNetUtil.parseBssid2bytes(viewModel.bssid);
        CharSequence devCountStr = mViewModel.deviceCountEdit.getText();
        byte[] deviceCount = devCountStr == null ? new byte[0] : devCountStr.toString().getBytes();
        byte[] broadcast = {(byte) (mViewModel.packageModeGroup.getCheckedRadioButtonId() == R.id.packageBroadcast
                ? 1 : 0)};

        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(this, mainHandler);
        mTask.execute(ssid, bssid, password, deviceCount, broadcast);
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private WeakReference<WifiPassActivity> mActivity;

        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        private Handler handler;

        EsptouchAsyncTask4(WifiPassActivity activity, Handler handler) {
            mActivity = new WeakReference<>(activity);
            this.handler = handler;
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
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            WifiPassActivity activity = mActivity.get();
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
            WifiPassActivity activity = mActivity.get();
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
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
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
            apSsidTV.setText(ssid);
            apBssidTV.setText(bssid);
            messageView.setText(message);
            confirmBtn.setEnabled(confirmEnable);
        }
    }

}
