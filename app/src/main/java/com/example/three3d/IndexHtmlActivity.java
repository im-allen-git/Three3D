package com.example.three3d;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.three3d.util.HtmlUtil;
import com.example.three3d.util.WebHost;

import java.util.Objects;

public class IndexHtmlActivity extends AppCompatActivity {

    private Context context;

    private static final int WRITE_REQ = 1001;
    private static final int READ_REQ = 1002;
    private static boolean isReadPermissions = false;
    private static boolean isWritePermissions = false;

    private String WEB_URL;
    private WebHost webHost;

    @SuppressLint({"SourceLockedOrientationActivity", "AddJavascriptInterface", "SetJavaScriptEnabled"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIsPermission();

        setContentView(R.layout.index);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        // 拿到webView组件
        WebView webView = findViewById(R.id.index);

        // 拿到webView的设置对象
        WebSettings settings = webView.getSettings();
        // settings.setAppCacheEnabled(true); // 开启缓存
        settings.setJavaScriptEnabled(true); // 开启javascript支持

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        WebHost webHost = new WebHost(this, mainHandler);
        //JS映射
        webView.addJavascriptInterface(webHost, "js");

        // 复写WebViewClient类的shouldOverrideUrlLoading方法
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new GoogleClient());


        Intent intent = getIntent();
        // 获取到传递参数
        WEB_URL = intent.getStringExtra("url");
        if (WEB_URL == null || WEB_URL.length() == 0) {
            WEB_URL = HtmlUtil.INDEX_HTML;
        }
        webView.loadUrl(WEB_URL);
    }

    public class MyWebViewClient extends WebViewClient {
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

    @Override
    protected void onResume() {

        super.onResume();
        // this.onCreate(null);
        this.recreate();
    }

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    actionKey(KeyEvent.KEYCODE_BACK);
                    break;
            }
        }
    };

    /**
     * 模拟键盘事件方法
     *
     * @param keyCode
     */
    public void actionKey(final int keyCode) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
