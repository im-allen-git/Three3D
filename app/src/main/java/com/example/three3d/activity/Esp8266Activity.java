package com.example.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.R;
import com.example.three3d.util.StlUtil;
import com.example.three3d.util.WebHost;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Esp8266Activity extends AppCompatActivity {

    private WebHost webHost;

    @SuppressLint({"SourceLockedOrientationActivity", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.esp8266);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // 拿到webView组件
        WebView webView = findViewById(R.id.esp8266_view);

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
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new GoogleClient());
        Intent intent = getIntent();
        // 获取到传递参数
        /*String esp8266url = intent.getStringExtra("esp8266url");
        System.err.println("esp8266url:" + esp8266url);
        if (esp8266url == null || esp8266url.length() == 0) {
            esp8266url = "http://10.0.0.113/";
        }*/
        webView.loadUrl(StlUtil.ESP_8266_URL);

        String filePath = intent.getStringExtra("filePath");

        if (filePath != null && filePath.length() > 0) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                List<File> listFile = new ArrayList<>();
                listFile.add(file);
                webView.loadUrl("javascript:files_check_if_upload_files(" + listFile + ")");
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

            }
        }
    };

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }


    public class GoogleClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }
    }
}
