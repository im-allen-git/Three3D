package com.kairong.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.kairong.three3d.R;
import com.kairong.three3d.config.HtmlConfig;
import com.kairong.three3d.util.ActivityCollector;
import com.kairong.three3d.util.WebHost;
import com.kairong.three3d.util.WebViewClientUtil;

import java.util.Objects;

public class UploadGcodeActivity extends AppCompatActivity {

    private String WEB_URL = HtmlConfig.UPLOAD_GCODE_HTML;
    private WebHost webHost;
    WebView webView;
    private Context context;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.upload_gcode);

        context = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // 拿到webView组件
        webView = findViewById(R.id.indexShop);

        // 拿到webView的设置对象
        WebSettings settings = webView.getSettings();
        // settings.setAppCacheEnabled(true); // 开启缓存
        settings.setJavaScriptEnabled(true); // 开启javascript支持

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webHost = new WebHost(this, mainHandler);
        //JS映射
        webView.addJavascriptInterface(webHost, "js");

        // 复写WebViewClient类的shouldOverrideUrlLoading方法
        webView.setWebViewClient(WebViewClientUtil.getMyWebViewClient());
        webView.setWebChromeClient(WebViewClientUtil.getGoogleClient());
        webView.loadUrl(WEB_URL);


    }

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent it = new Intent(context.getApplicationContext(), Esp8266Activity.class);
                    it.putExtra("filePath", msg.obj.toString());
                    context.startActivity(it);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
