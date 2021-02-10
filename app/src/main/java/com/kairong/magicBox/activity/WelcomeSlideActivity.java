package com.kairong.magicBox.activity;

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

import com.kairong.magicBox.R;
import com.kairong.magicBox.config.HtmlConfig;
import com.kairong.magicBox.util.ActivityCollector;
import com.kairong.magicBox.util.WebHost;
import com.kairong.magicBox.util.WebViewClientUtil;

import java.util.Objects;

public class WelcomeSlideActivity extends AppCompatActivity {

    private Context context;
    private String WEB_URL;
    private WebHost webHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.welcome_slide);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        context = this;

        // 拿到webView组件
        WebView webView = findViewById(R.id.welcome_slid);
        WebHost.disableLongClick(webView);
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

        Intent intent = getIntent();
        // 获取到传递参数
        WEB_URL = intent.getStringExtra("url");
        if (WEB_URL == null || WEB_URL.length() == 0) {
            WEB_URL = HtmlConfig.WELCOME_SLIDE_HTML;
        }
        webView.loadUrl(WEB_URL);
    }

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
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
