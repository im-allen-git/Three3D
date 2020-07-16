package com.kairong.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.kairong.three3d.R;
import com.kairong.three3d.pojo.StlGcode;
import com.kairong.three3d.util.HtmlUtil;
import com.kairong.three3d.util.IOUtil;
import com.kairong.three3d.util.OkHttpUtil;
import com.kairong.three3d.util.PermissionCheckUtil;
import com.kairong.three3d.util.StlUtil;
import com.kairong.three3d.util.WebHost;
import com.kairong.three3d.util.WebViewClientUtil;
import com.kairong.three3d.util.ZipFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BulidModuleActivity extends AppCompatActivity {


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

        PermissionCheckUtil.checkIsPermission(this, this);
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
        webView.setWebViewClient(WebViewClientUtil.getMyWebViewClient());
        webView.setWebChromeClient(WebViewClientUtil.getGoogleClient());
//        webView.loadUrl(HtmlUtil.BULID_MODULE_URL);
        WebHost.disableLongClick(webView);
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
        if (PermissionCheckUtil.isReadPermissions) {

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
        if (PermissionCheckUtil.isReadPermissions && PermissionCheckUtil.isWritePermissions) {
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
        if (PermissionCheckUtil.isReadPermissions && PermissionCheckUtil.isWritePermissions) {
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


                        String nwName = stlGcode.getSourceStlName().replace(".stl", ".gco");
                        ;

                        // File nwfile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), nwName);
                        // String renameFilePath = nwfile.getAbsolutePath();

                        String renameFilePath = IOUtil.DOWN_LOAD_PATH + "/printer3d/" + nwName;

                        File nwfile = new File(renameFilePath);

                        if (!nwfile.getParentFile().exists() || !nwfile.getParentFile().isDirectory()) {
                            nwfile.getParentFile().mkdirs();
                        }

                        // unGcodeZip.renameTo(nwfile);

                        System.err.println("gco:" + renameFilePath);

                        IOUtil.copyFile(unGcodeZip.getAbsolutePath(), renameFilePath);


                        stlGcode.setLocalGcodeName(unGcodeZipPath);

                        File renameFile = new File(renameFilePath);

                        if (renameFile.exists() && renameFile.isFile()) {
                            System.err.println("gco copy:" + renameFilePath + ", true");
                            unGcodeZip.deleteOnExit();
                            stlGcode.setLocalGcodeName(renameFilePath);
                        } else {
                            System.err.println("gco copy:" + renameFilePath + ", false");
                        }

                        IOUtil.getGoceInfo(stlGcode);
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


}
