package com.kairong.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kairong.three3d.R;
import com.kairong.three3d.config.PrinterConfig;
import com.kairong.three3d.pojo.ClientWebSocketListener;
import com.kairong.three3d.pojo.StlGcode;
import com.kairong.three3d.util.ActivityCollector;
import com.kairong.three3d.util.CacheUtil;
import com.kairong.three3d.util.DialogUtil;
import com.kairong.three3d.util.OkHttpUtil;
import com.kairong.three3d.util.PermissionCheckUtil;
import com.kairong.three3d.util.PrinterUtil;
import com.kairong.three3d.util.StlDealUtil;
import com.kairong.three3d.util.WebViewClientUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrinterStartActivity extends AppCompatActivity {

    private static final String TAG = PrinterStartActivity.class.getSimpleName();

    private TextView printerName, status_waiting, textView, textViewTimer;
    private ImageView imageView, printingItem;
    private Button reTryButton;


    private Context context;

    private OkHttpClient mOkHttpClient;

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what < 100) {
                textView.setText(msg.what + "%");
                textViewTimer.setText(msg.obj.toString());
            } else if (msg.what >= 100 && msg.what < 110) {
                textView.setText("打印完成!");
                textViewTimer.setText(msg.obj.toString());
            } else if (msg.what == 120) {
                status_waiting.setText("请勿关闭当前页面!");
                textView.setText(msg.obj.toString());
            } else if (msg.what == 130) {
                System.err.println("-------------130:" + msg.obj.toString());
                PrinterUtil.isRun = false;
                textView.setText(msg.obj.toString());
                PrinterUtil.tempStlGcode.setFlag(1);
                StlDealUtil.stlMap.put(PrinterUtil.tempStlGcode.getRealStlName(), PrinterUtil.tempStlGcode);
                StlDealUtil.updateModuleDataBase(context, PrinterUtil.tempStlGcode.getRealStlName());
                new Thread() {
                    @Override
                    public void run() {
                        PrinterConfig.is_background = 0;
                        PrinterUtil.printNow(PrinterUtil.tempStlGcode.getShortGcode(), PrinterUtil.tempStlGcode, mainHandler);
                    }
                }.start();
            } else if (msg.what == 140) {
                PrinterUtil.isRun = false;
                reTryButton.setVisibility(View.VISIBLE);
                textViewTimer.setVisibility(View.GONE);
                textView.setText(msg.obj.toString());
            } else if (msg.what == 150) {
                textView.setText("请重试...");
            }
        }
    };


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.printer_status);
        context = this;

        PrinterUtil.isRun = true;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();

        if (CacheUtil.sdList.size() == 0) {
            CacheUtil.getSdListThread(1);
        }
        PrinterConfig.upload_count = 0;
        PrinterConfig.printer_count = 0;


        // 判断传递的参数并且放入队列
        if (PrinterConfig.upload_count > 0 || PrinterConfig.printer_count > 0 || PrinterUtil.beforePrinter(this)) {
            if (PrinterConfig.is_background > 0) {
                Intent it = this.getIntent();
                String gcodeName = it.getStringExtra("gcodeName");
                String flag = it.getStringExtra("flag");
                if (TextUtils.isEmpty(gcodeName) || PrinterConfig.currPrinterGcodeInfo == null) {
                    DialogUtil.showUpload(this, "存在处理文件，请排队!");
                } else {
                    StlGcode tempStlGcode = null;
                    if ("0".equalsIgnoreCase(flag)) {
                        tempStlGcode = StlDealUtil.localMapStl.get(gcodeName);
                    } else if ("1".equalsIgnoreCase(flag)) {
                        tempStlGcode = StlDealUtil.stlDataBaseMap.get(gcodeName);
                    }
                    if (tempStlGcode != null && tempStlGcode.getShortGcode().equalsIgnoreCase(PrinterConfig.currPrinterGcodeInfo.getStlGcode().getShortGcode())) {
                        DialogUtil.showUpload(this, "当前文件正在处理!");
                    } else {
                        DialogUtil.showUpload(this, "存在处理文件，请排队!");
                    }
                }
            }

            // 显示打印gcode图片信息
            PrinterUtil.setPrinterInfo(context, printerName, imageView, printingItem,
                    status_waiting, textView, textViewTimer, PrinterUtil.tempStlGcode);
            // 执行上传或者打印界面的时间显示
            printAnimation();
        } else {
            printerName.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            // status_waiting.setVisibility(View.INVISIBLE);
            printingItem.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            textViewTimer.setVisibility(View.INVISIBLE);
            status_waiting.setText("获取模型失败!");
        }
    }


    private void initView() {
        ImageButton ModuleParamBtn = findViewById(R.id.imageButtonModuleParam);
        ModuleParamBtn.setOnClickListener(v -> {
            WebViewClientUtil.actionKey(KeyEvent.KEYCODE_BACK);
        });

        printerName = findViewById(R.id.printerName);
        imageView = findViewById(R.id.imageView);
        status_waiting = findViewById(R.id.status_waiting);
        printingItem = findViewById(R.id.printingItem);
        textView = findViewById(R.id.textView);
        textViewTimer = findViewById(R.id.textViewTimer);
        reTryButton = findViewById(R.id.retryBtn);
        reTryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrinterUtil.isRun = true;
                textViewTimer.setVisibility(View.VISIBLE);
                reTryButton.setVisibility(View.GONE);
                new Thread() {
                    @Override
                    public void run() {
                        postTo3dPrinter(PrinterUtil.tempStlGcode);
                    }
                }.start();
            }
        });
    }

    private void getWebSocket() {
        mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(PrinterConfig.ESP_8266_URL.replace("http:", "ws:"))
                .build();
        ClientWebSocketListener listener = new ClientWebSocketListener();
        mOkHttpClient.newWebSocket(request, listener);
        mOkHttpClient.dispatcher().executorService().shutdown();
    }


    private void printAnimation() {

        if (!CacheUtil.sdMap.containsKey(PrinterUtil.tempStlGcode.getShortGcode().toUpperCase())
                && PrinterUtil.tempStlGcode.getFlag() == 0) {
            postTo3dPrinter(PrinterUtil.tempStlGcode);
            /*new Thread() {
                @Override
                public void run() {
                    postTo3dPrinter(PrinterUtil.tempStlGcode);
                }
            }.start();*/
        } else {
            new Thread() {
                @Override
                public void run() {
                    PrinterUtil.printNow(PrinterUtil.tempStlGcode.getShortGcode(), PrinterUtil.tempStlGcode, mainHandler);
                }
            }.start();
        }
    }


    // okhttps 上传文件到3d打印机
    public boolean postTo3dPrinter(StlGcode stlGcode) {
        boolean isSu = false;
        if (PermissionCheckUtil.isReadPermissions) {
            if (stlGcode != null && PrinterConfig.ESP_8266_URL != null && PrinterConfig.ESP_8266_URL.length() > 0) {

                File file = new File(stlGcode.getLocalGcodeName());
                if (file.exists() && file.isFile()) {
                    Request request = OkHttpUtil.getRequestByBody(file, PrinterUtil.getPostFileUrl());
                    OkHttpUtil.sendMessage(120, "开始上传", mainHandler);
                    // OkHttpClient client = OkHttpUtil.getClient();
                    // PrinterConfig.currPrinterGcodeInfo.setBegin_time(System.currentTimeMillis());

                    Thread myThread = PrinterUtil.getShowThread(mainHandler, stlGcode);
                    myThread.start();

                    if (PrinterConfig.is_background == 0) {
                        OkHttpClient client = OkHttpUtil.getClient(mainHandler, textView);
                        PrinterConfig.is_background = 1;
                        try {
                            client.newCall(request).enqueue(new Callback() {

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    PrinterUtil.isRun = false;
                                    String content = response.body().string();
                                    System.err.println("-------" + content);
                                    if (content != null && (content.contains("Ok") || content.contains("ok"))) {
                                        OkHttpUtil.sendMessage(100, "上传成功", mainHandler);
                                        OkHttpUtil.sendMessage(130, "上传成功", mainHandler);
                                        CacheUtil.getSdListThread(1);
                                    } else {
                                        PrinterConfig.is_background = 0;
                                        OkHttpUtil.sendMessage(140, "上传失败", mainHandler);
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    PrinterUtil.isRun = false;
                                    PrinterConfig.is_background = 0;
                                    System.err.println(e.getMessage());
                                    if (Objects.requireNonNull(e.getMessage()).contains("timeout")) {
                                        OkHttpUtil.sendMessage(140, "超时", mainHandler);
                                    } else if (e.getMessage().contains("打印机连接SD卡异常")) {
                                        OkHttpUtil.sendMessage(140, "超时", mainHandler);
                                    } else {
                                        OkHttpUtil.sendMessage(140, "上传失败", mainHandler);
                                    }
                                }
                            });

                            // Thread.sleep(20* 1000);
                            // PrinterUtil.isRun = false;
                        /*myThread.start();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            myThread.interrupt();
                            isSu = true;
                            OkHttpUtil.sendMessage(130, "上传成功", mainHandler);
                            System.err.println("postTo3dPrinter,rs:" + response.body().string());
                        } else {
                            OkHttpUtil.sendMessage(140, "上传失败", mainHandler);
                            System.err.println("postTo3dPrinter,rs: error !!!" + response.body().string());
                            myThread.interrupt();
                        }*/
                            // System.err.println("postTo3dPrinter,rs:" + response.body().string());
                        } catch (Exception e) {
                            PrinterConfig.is_background = 0;
                            PrinterUtil.isRun = false;
                            e.printStackTrace();
                            Log.e(TAG, "postTo3dPrinter,error:", e);
                            OkHttpUtil.sendMessage(140, "上传失败", mainHandler);
                        }
                    }
                } else {
                    PrinterConfig.is_background = 0;
                    OkHttpUtil.sendMessage(140, "获取文件失败", mainHandler);
                }
            } else {
                PrinterConfig.is_background = 0;
                OkHttpUtil.sendMessage(140, "获取打印机连接失败", mainHandler);
            }
        }
        return isSu;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
