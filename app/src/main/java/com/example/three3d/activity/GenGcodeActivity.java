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
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.example.three3d.R;
import com.example.three3d.pojo.StlGcode;
import com.example.three3d.util.HttpsTrustManager;
import com.example.three3d.util.IOUtil;
import com.example.three3d.util.ProgressResponseBody;
import com.example.three3d.util.StlUtil;
import com.example.three3d.util.ZipFileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GenGcodeActivity extends AppCompatActivity {

    private static final String FILE_UPLOAD_URL = "https://192.168.1.67:448/file/uploadFileAndGenGcode";
    private static final String FILE_DOWN_URL = "https://192.168.1.67:448/file/downloadFile?fileName=";

    private String currentFileName;

    private static final int WRITE_REQ = 1001;
    private static final int READ_REQ = 1002;
    private static boolean isReadPermissions = false;
    private static boolean isWritePermissions = false;

    private static final int UPLOAD_COMPLETED = 3001;
    private static final int UPLOAD_ERROR = 3002;
    private static final int DOWN_COMPLETED = 3010;
    private static final int DOWN_ERROR = 3012;

    private static final int UPLOAD_PROGRESS = 3030;
    private static final int DOWN_PROGRESS = 3040;

    private StlGcode stlGcode;

    private Context context;


    private Button upBtn, downBtn, listBtn;
    private ProgressBar dwProgress;
    private TextView fileText;
    private SimpleAdapter sim_adapter;


    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOAD_COMPLETED:
                    // 上传完成
                    //downBtn.setVisibility(View.VISIBLE);
                    fileText.setText(msg.obj.toString());
                    //upProgress.setVisibility(View.INVISIBLE);
                    break;
                case UPLOAD_ERROR:
                    // 上传失败
                    // downBtn.setVisibility(View.INVISIBLE);
                    fileText.setText(msg.obj.toString());
                    break;
                case DOWN_COMPLETED:
                    // 下载完成
                case DOWN_ERROR:
                    // 下载失败
                    fileText.setText(msg.obj.toString());
                    dwProgress.setVisibility(View.INVISIBLE);
                    break;
                case DOWN_PROGRESS:
                    dwProgress.setVisibility(View.VISIBLE);
                    dwProgress.setProgress(Integer.parseInt(msg.obj.toString()));
                    break;
            }
        }
    };

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();// 隐藏标题栏
        setContentView(R.layout.gen_gcode);

        checkIsPermission();

        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;

        Intent intent = getIntent();
        // 获取到传递参数
        currentFileName = intent.getStringExtra("fileName");


        stlGcode = StlUtil.stlMap.get(currentFileName);
        if (stlGcode == null) {
            System.err.println("无数据");
            return;
        }
        fileText = findViewById(R.id.file_text_show);
        fileText.setText(stlGcode.getSourceStlName());


        //downText.setVisibility(View.INVISIBLE);
        upBtn = findViewById(R.id.up_button);
        // upBtn.setVisibility(View.INVISIBLE);


        listBtn = findViewById(R.id.show_list_button);
//        listBtn.setVisibility(View.INVISIBLE);


        dwProgress = findViewById(R.id.progress_bar_dw);
        //dwProgress.setVisibility(View.INVISIBLE);
        downBtn = findViewById(R.id.down_button);
        downBtn.setVisibility(View.INVISIBLE);


        addUpBtnListener();
        // addDwBtnListener();

        showListListener();
    }

    private void initGrid() {

        List<Map<String, Object>> data_list = StlUtil.getDataList();
        //新建适配器
        String[] from = {"id", "sourceStlName", "realStlName", "createTime"};
        int[] to = {1, 2, 3, 4};

        GridView gv = findViewById(R.id.stl_list_grid);

        sim_adapter = new SimpleAdapter(this, data_list, R.layout.grid_item, from, to);
        //配置适配器
        gv.setAdapter(sim_adapter);

    }


    // 上传文件响应
    private void addUpBtnListener() {
        upBtn.setOnClickListener(v -> {

            if (isReadPermissions && isWritePermissions) {
                doUpload();
            }
        });
    }

    // 下载文件响应
    private void addDwBtnListener() {
        downBtn.setOnClickListener(v -> {

            downFile();

        });
    }

    // 显示文件
    private void showListListener() {
        listBtn.setOnClickListener(v -> {

            initGrid();
        });
    }


    private void doUpload() {
        String zipFile = stlGcode.getSourceZipStlName();
        if (null != zipFile && zipFile.length() > 1) {
            if (null != zipFile && zipFile.length() > 0) {
                fileText.setText("正在上传");
                Thread upThread = new Thread(() -> {
                    File stlFile = new File(zipFile);
                    postProgress(FILE_UPLOAD_URL, stlFile, new HashMap<>());
                    downFile();
                });
                upThread.start();
            }
        } else {
            sendMessage(UPLOAD_ERROR, "获取文件失败");
        }
    }

    // okhttps 上传文件
    public void postProgress(String url, File file, Map<String, String> commandLineMap) {
        if (isReadPermissions) {
            sendMessage(UPLOAD_PROGRESS, "0");
            String multipartStr = "multipart/form-data";
            RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse(multipartStr), file))
                    .addFormDataPart("commandLineMap", commandLineMap.toString()).build();
            Request request = new Request.Builder().url(url).post(formBody).build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory(), new HttpsTrustManager())
                    .hostnameVerifier(new HttpsTrustManager.TrustAllHostnameVerifier())
                    .readTimeout(600, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .connectTimeout(1200, TimeUnit.SECONDS).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                    System.err.println(jsonObject);
                    String currentGcode = "";
                    if (null != jsonObject && 200 == jsonObject.getIntValue("code")) {
                        currentGcode = jsonObject.getString("data");
                        stlGcode.setServerZipGcodeName(currentGcode);
                        sendMessage(UPLOAD_COMPLETED, currentGcode);
                    } else {
                        sendMessage(UPLOAD_ERROR, "上传失败，请重试");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendMessage(int what, String message) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = message;
        mainHandler.sendMessage(msg);
    }


    private void downFile() {
        if (isReadPermissions && isWritePermissions) {
            String currentGcodeZip = stlGcode.getServerZipGcodeName();
            if (null != currentGcodeZip && currentGcodeZip.length() > 0) {
                Thread dwThread = new Thread(() -> {
                    String outFileName = currentFileName.replace("stl", "gcode") + ".zip";
                    downloadProgress(FILE_DOWN_URL + currentGcodeZip, outFileName);
                });
                dwThread.start();
            } else {
                sendMessage(DOWN_ERROR, currentFileName + ", 没有gcode");
                // System.err.println("currentFileName:" + currentFileName + ", 没有gcode");
            }
        }
    }

    private void downloadProgress(String url, String outfileName) {
        if (isReadPermissions && isWritePermissions) {
            sendMessage(DOWN_PROGRESS, "0");
            try {
                //构建一个请求
                Request request = new Request.Builder().addHeader("Connection", "close")
                        .addHeader("Accept", "*/*")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                        .get().url(url).build();

                //构建我们的进度监听器
                final ProgressResponseBody.ProgressListener listener = (bytesRead, contentLength, done) -> {
                    //计算百分比并更新ProgressBar
                    final int percent = (int) (100 * bytesRead / contentLength);
                    sendMessage(DOWN_PROGRESS, String.valueOf(percent));
                    // System.out.println("下载进度：" + (100 * bytesRead) / contentLength + "%");
                };
                //创建一个OkHttpClient，并添加网络拦截器
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory(), new HttpsTrustManager())
                        .hostnameVerifier(new HttpsTrustManager.TrustAllHostnameVerifier())
                        .readTimeout(600, TimeUnit.SECONDS)
                        .writeTimeout(600, TimeUnit.SECONDS)
                        .connectTimeout(1200, TimeUnit.SECONDS)
                        .addNetworkInterceptor(chain -> {
                            Response response = chain.proceed(chain.request());
                            //这里将ResponseBody包装成我们的ProgressResponseBody
                            return response.newBuilder()
                                    .body(new ProgressResponseBody(response.body(), listener))
                                    .build();
                        }).build();
                //发送响应
                Call call = client.newCall(request);
                // 同步
                Response response = call.execute();
                if (response.isSuccessful()) {
                    File gcodeFile = new File(outfileName);
                    writeToFile(response, gcodeFile);

                    // 下载完成
                    File tempFile = new File(gcodeFile.getAbsolutePath());
                    boolean b = tempFile.exists() && !tempFile.isDirectory();
                    if (b) {
                        sendMessage(DOWN_COMPLETED, "下载成功," + gcodeFile.getName());
                        System.err.println(gcodeFile.getAbsolutePath() + ",result:" + b);

                        stlGcode.setServerZipGcodeName(gcodeFile.getAbsolutePath());
                        // 解压zip文件
                        String unGcodeZipPath = gcodeFile.getAbsolutePath().replace(".zip", "");
                        ZipFileUtil.upZipFile(tempFile, tempFile.getParentFile().getAbsolutePath().replace("\\", "/"));

                        File unGcodeZip = new File(unGcodeZipPath);
                        if (unGcodeZip.exists() && unGcodeZip.isFile()) {
                            stlGcode.setLocalGcodeName(unGcodeZipPath);

                            // StlUtil.stlMap.put(stlGcode.getRealStlName(), stlGcode);
                            // 保存到数据库
                            StlGcode tempCode = StlUtil.stlMap.get(currentFileName);
                            StlUtil.updateModuleDataBase(context, currentFileName);
                            sendMessage(DOWN_COMPLETED, "解压成功");
                        } else {
                            System.err.println(unGcodeZipPath + ",解压失败");
                            sendMessage(DOWN_COMPLETED, "解压失败");
                        }
                    }
                    System.err.println(gcodeFile.getAbsolutePath() + ",result:" + b);
                } else{
                    sendMessage(DOWN_COMPLETED, "下载失败," + outfileName);
                }

                // 异步
                /*call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        sendMessage(DOWN_ERROR, "下载失败，请重试");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            long length = response.body().contentLength();
                            File gcodeFile = new File(outfileName);
                            if (length == 0) {

                            }
                            //从响应体读取字节流
                            writeToFile(response, gcodeFile);

                        } else {
                            sendMessage(DOWN_ERROR, "下载失败，请重试");
                        }
                    }
                });*/
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("execute download[ " + url + "] error:" + e.getMessage());
                Log.e("downFile", "error:", e);
            }
        }
    }

    private void writeToFile(Response response, File gcodeFile) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream output = null;

        try {
            inputStream = response.body().byteStream();
            fileOutputStream = new FileOutputStream(gcodeFile);
            output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            output.flush();
            output.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeAll(inputStream, fileOutputStream, output);
        }

    }

    /**
     * 检查读取和写入权限
     */
    private void checkIsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isWritePermissions = true;
        } else {
            isWritePermissions = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQ);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isReadPermissions = true;
        } else {
            isReadPermissions = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQ);
        }
    }

    /***
     * 申请权限后的回调函数
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_REQ) {
            if (null != grantResults && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isWritePermissions = true;
            } else {
                isWritePermissions = false;
            }
        }

        if (requestCode == READ_REQ) {
            if (null != grantResults && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isReadPermissions = true;
            } else {
                isReadPermissions = false;
            }
        }
    }
}
