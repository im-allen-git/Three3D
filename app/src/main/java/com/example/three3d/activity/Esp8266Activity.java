package com.example.three3d.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.three3d.R;
import com.example.three3d.util.StlUtil;
import com.example.three3d.util.WebHost;

import java.io.File;
import java.util.Objects;

public class Esp8266Activity extends AppCompatActivity {


    private static final int WRITE_REQ = 1001;
    private static final int READ_REQ = 1002;
    private static boolean isReadPermissions = false;
    private static boolean isWritePermissions = false;


    private WebHost webHost;
    private WebView webView;

    @SuppressLint({"SourceLockedOrientationActivity", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.esp8266);

        checkIsPermission();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // 拿到webView组件
        webView = findViewById(R.id.esp8266_view);

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
        // 获取到传递参数
        /*String esp8266url = intent.getStringExtra("esp8266url");
        System.err.println("esp8266url:" + esp8266url);
        if (esp8266url == null || esp8266url.length() == 0) {
            esp8266url = "http://10.0.0.113/";
        }*/
        webView.loadUrl(StlUtil.ESP_8266_URL);

    }


    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 11) {
                uploadGcode(msg.obj.toString());
            }
        }
    };


    /**
     * 调用js方法上传文件
     *
     * @param filePath
     */
    private void uploadGcode(String filePath) {
        if (filePath != null && filePath.length() > 0) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // webView.loadUrl("javascript:files_check_if_upload_files(" + filePath + ")");
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }


    }


    public class GoogleClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }


    /**
     * 检查读取和写入权限
     */
    private void checkIsPermission() {
        //CameraDemoActivity 是activity的名字
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //有权限的情况
            isWritePermissions = true;
        } else {
            //没有权限，进行权限申请
            //REQ是本次请求的辨认编号,即 requestCode
            isWritePermissions = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQ);
        }

        //CameraDemoActivity 是activity的名字
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //有权限的情况
            isReadPermissions = true;
        } else {
            //没有权限，进行权限申请
            //REQ是本次请求的辨认编号,即 requestCode
            isReadPermissions = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQ);
        }
    }
}
