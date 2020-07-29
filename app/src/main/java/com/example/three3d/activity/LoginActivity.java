package com.example.three3d.activity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.three3d.R;
import com.example.three3d.pojo.UserPojo;
import com.example.three3d.util.HtmlUtil;
import com.example.three3d.util.OkHttpUtil;
import com.example.three3d.util.WebHost;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private String WEB_URL;
    private WebHost webHost;
    WebView webView;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_account);

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
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new GoogleClient());
        WebHost.disableLongClick(webView);
        Intent intent = getIntent();
        // 获取到传递参数
        WEB_URL = intent.getStringExtra("url");
        if (WEB_URL == null || WEB_URL.length() == 0) {
            WEB_URL = HtmlUtil.MYMODULE_HTML;
        }
        webView.loadUrl(WEB_URL);


        //数据同步
//        dataSync(String.valueOf(webHost.getUserId("userId")));
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
        webView.loadUrl(WEB_URL);
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
     * 客户端数据与服务器同步
     *
     * @param userId
     */
    public void dataSync(String userId) {

        List<UserPojo> userListSync =  webHost.getUserListSync(userId);
        dataProgress(OkHttpUtil.USER_URL,userListSync);

    }

    // 请求服务器处理
    private void dataProgress(String url, List<UserPojo> userListSync) {

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("userListSync", JSON.toJSONString(userListSync)).build();

            final Request request = new Request.Builder()
                    .url(url)
                    .post(body).build();

            //发送响应
            Call call = client.newCall(request);
            // 同步
            Response response = call.execute();
            if (response.isSuccessful()) {
                dataSyncResult(response.body().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("execute dataProgress[ " + url + "] error:" + e.getMessage());
            Log.e("dataProgress", "error:", e);
        }

    }

    /**
     * 客户端数据与服务器同步返回结果操作
     *
     * @param resultStr
     */
    public void dataSyncResult(String resultStr) {

        JSONArray.parseArray(resultStr,UserPojo.class);
//        List<UserPojo> userListSync =  webHost.getUserListSync(userId);
//        dataProgress(OkHttpUtil.USER_URL,userListSync);

    }

}
