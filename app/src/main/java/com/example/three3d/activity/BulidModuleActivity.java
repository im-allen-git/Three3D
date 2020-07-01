package com.example.three3d.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.example.three3d.R;
import com.example.three3d.pojo.StlGcode;
import com.example.three3d.util.HtmlUtil;
import com.example.three3d.util.OkHttpUtil;
import com.example.three3d.util.StlUtil;
import com.example.three3d.util.WebHost;
import com.example.three3d.util.ZipFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BulidModuleActivity extends AppCompatActivity {


    private static final int WRITE_REQ = 1001;
    private static final int READ_REQ = 1002;
    private static boolean isReadPermissions = false;
    private static boolean isWritePermissions = false;

    private WebHost webHost;
    private Context context;
    private String WEB_URL;

    private static final String TAG = BulidModuleActivity.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                dealStl();
            }
        }
    };

    @SuppressLint({"SourceLockedOrientationActivity", "AddJavascriptInterface", "SetJavaScriptEnabled"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIsPermission();
        setContentView(R.layout.bulid_module);

        context = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new GoogleClient());
//        webView.loadUrl(HtmlUtil.BULID_MODULE_URL);
        Intent intent = getIntent();
        // 获取到传递参数
        WEB_URL = intent.getStringExtra("url");
        if (WEB_URL == null || WEB_URL.length() == 0) {
            WEB_URL = HtmlUtil.BULID_MODULE_URL;
        }
        webView.loadUrl(WEB_URL);

    }


    private void dealStl() {
        // 保存成功后，自动执行上传文件命令
        StlGcode stlGcode = StlUtil.stlMap.get(webHost.getCurrentFileName());
        if (stlGcode != null && stlGcode.getRealStlName() != null) {
            OkHttpUtil.getThreadPoolExecutor().execute(() -> {
                boolean isSu = false;
                int count = 0;
                while (!isSu && count < 4) {
                    isSu = doUpload(stlGcode);
                    if (!isSu) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count++;
                    }
                }
            });
        }
    }


    private boolean doUpload(StlGcode stlGcode) {
        boolean isSu = false;
        String zipFile = stlGcode.getSourceZipStlName();
        if (null != zipFile && zipFile.length() > 0) {
            System.out.println("...正在上传...");
            File stlFile = new File(zipFile);
            if (postProgress(OkHttpUtil.FILE_UPLOAD_URL, stlFile, new HashMap<>(), stlGcode)) {
                isSu = downFile(stlGcode);
            }
        } else {
            System.err.println("!!!获取文件失败!!!");
            Log.e(TAG, "!!!获取文件失败!!!");
        }
        return isSu;
    }


    // okhttps 上传文件
    public boolean postProgress(String url, File file, Map<String, String> commandLineMap, StlGcode stlGcode) {
        boolean isSu = false;
        if (isReadPermissions) {

            Request request = OkHttpUtil.getRequestByBody(file, commandLineMap, url);
            OkHttpClient client = OkHttpUtil.getClient();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                    System.err.println(jsonObject);
                    String currentGcode = "";
                    if (null != jsonObject && 200 == jsonObject.getIntValue("code")) {
                        currentGcode = jsonObject.getString("data");
                        stlGcode.setServerZipGcodeName(currentGcode);
                        System.out.println("...上传成功...");
                        isSu = true;
                    } else {
                        System.err.println("!!!上传失败，请重试!!!");
                        Log.e(TAG, "!!!上传失败，请重试!!!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "postProgress,error:", e);
            }
        }
        return isSu;
    }


    private boolean downFile(StlGcode stlGcode) {
        boolean isSu = false;
        if (isReadPermissions && isWritePermissions) {
            String currentGcodeZip = stlGcode.getServerZipGcodeName();
            if (null != currentGcodeZip && currentGcodeZip.length() > 0) {
                String outFileName = stlGcode.getRealStlName().replace("stl", "gcode") + ".zip";
                isSu = downloadProgress(OkHttpUtil.FILE_DOWN_URL + currentGcodeZip, outFileName, stlGcode);
            } else {
                System.err.println("!!!currentFileName:" + stlGcode.getRealStlName() + ", 没有gcode!!!");
                Log.e(TAG, "!!!currentFileName:" + stlGcode.getRealStlName() + ", 没有gcode!!!");
            }
        }
        return isSu;
    }

    private boolean downloadProgress(String url, String outfileName, StlGcode stlGcode) {
        boolean isSu = false;
        if (isReadPermissions && isWritePermissions) {
            try {
                //构建一个请求
                Request request = OkHttpUtil.getRequest(url);
                //创建一个OkHttpClient，并添加网络拦截器
                OkHttpClient client = OkHttpUtil.getClient();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    OkHttpUtil.writeToFile(response, new File(outfileName));
                }
                File tempFile = new File(outfileName);
                isSu = tempFile.exists() && tempFile.isFile();

                if (isSu) {
                    System.err.println(tempFile.getAbsolutePath() + ",result:" + isSu);

                    stlGcode.setServerZipGcodeName(tempFile.getAbsolutePath());
                    // 解压zip文件
                    String unGcodeZipPath = tempFile.getAbsolutePath().replace(".zip", "");
                    ZipFileUtil.upZipFile(tempFile, tempFile.getParentFile().getAbsolutePath().replace("\\", "/"));

                    File unGcodeZip = new File(unGcodeZipPath);
                    isSu = unGcodeZip.exists() && unGcodeZip.isFile();
                    if (isSu) {
                        String renameFilePath = unGcodeZipPath.substring(0, unGcodeZipPath.lastIndexOf("/"))
                                + "/" + stlGcode.getSourceStlName().replace(".stl",".gco");
                        unGcodeZip.renameTo(new File(renameFilePath));

                        stlGcode.setLocalGcodeName(unGcodeZipPath);
                        File renameFile = new File(renameFilePath);
                        if(renameFile.exists() && renameFile.isFile()){
                            unGcodeZip.deleteOnExit();
                            stlGcode.setLocalGcodeName(renameFilePath);
                        }

                        // 保存到数据库
                        StlUtil.updateModuleDataBase(context, stlGcode.getRealStlName());
                        System.out.println("..." + unGcodeZipPath + ",解压成功...");
                    } else {
                        System.err.println("!!!" + unGcodeZipPath + ",解压失败!!!");
                        Log.e(TAG, "downloadProgress: 解压失败");
                    }
                }
                System.err.println(tempFile.getAbsolutePath() + ",result:" + isSu);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("!!!execute download[ " + url + "] error:" + e.getMessage() + "!!!");
                Log.e(TAG, "execute download[ " + url + "],error:", e);
            }
        }
        return isSu;
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
