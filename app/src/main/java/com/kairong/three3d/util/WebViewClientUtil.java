package com.kairong.three3d.util;

import android.app.Instrumentation;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewClientUtil {

    public static MyWebViewClient getMyWebViewClient() {
        return new MyWebViewClient();
    }


    public static GoogleClient getGoogleClient() {
        return new GoogleClient();
    }

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //千万不能调用super的方法,super方法中默认取消了处理
            //super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }

    private static class GoogleClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }
    }


    /**
     * 模拟键盘事件方法
     *
     * @param keyCode
     */
    public static void actionKey(final int keyCode) {
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
}
