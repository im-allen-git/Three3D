package com.kairong.three3d.util;

import android.app.Instrumentation;
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
