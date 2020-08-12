package com.kairong.three3d.alipay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.PayTask;
import com.kairong.three3d.R;
import com.kairong.three3d.config.HtmlConfig;
import com.kairong.three3d.util.WebHost;
import com.kairong.three3d.util.WebViewClientUtil;

import java.util.Map;
import java.util.Objects;

public class AliPayRealActivity extends AppCompatActivity {
    private WebHost webHost;
    private Context context;
    private String WEB_URL;


    // 授权成功
    public static final int AUTH_SUC_CODE = 99;
    // 授权失败
    public static final int AUTH_ERR_CODE = 44;

    // 支付成功
    public static final int PAY_SUC_CODE = 1688;
    // 支付失败
    public static final int PAY_ERR_CODE = 1644;


    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTH_SUC_CODE:
                    // 调用服务器后，通知执行支付操作
                    payForAliPay(msg.obj.toString());
                    break;
                case AUTH_ERR_CODE:
                    // 授权失败操作
                    showAlert(context, getString(R.string.auth_failed));
                    break;
                case PAY_SUC_CODE: {
                    // 支付成功回调操作
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showAlert(context, getString(R.string.pay_success) + payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(context, getString(R.string.pay_failed) + payResult);
                    }
                }
                break;
                case PAY_ERR_CODE: {
                    // 授权失败操作
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        showAlert(context, getString(R.string.auth_success) + authResult);
                    } else {
                        // 其他状态值则为授权失败
                        showAlert(context, getString(R.string.auth_failed) + authResult);
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 拿到webView组件
        WebView webView = findViewById(R.id.children);
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

        WebHost.disableLongClick(webView);

        Intent intent = getIntent();
        WEB_URL = intent.getStringExtra("url");
        if (WEB_URL == null || WEB_URL.length() == 0) {
            WEB_URL = HtmlConfig.SERVER_SHOP_HTML;
        }
        webView.loadUrl(WEB_URL);
    }


    private void payForAliPay(String orderInfo) {

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(AliPayRealActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                Message msg = new Message();
                msg.what = AUTH_SUC_CODE;
                msg.obj = result;
                mainHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    @SuppressLint("NewApi")
    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }

}
