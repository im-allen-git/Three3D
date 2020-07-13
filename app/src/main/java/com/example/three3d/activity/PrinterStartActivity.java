package com.example.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.R;
import com.example.three3d.pojo.StlGcode;
import com.example.three3d.util.IOUtil;
import com.example.three3d.util.OkHttpUtil;
import com.example.three3d.util.PermissionCheckUtil;
import com.example.three3d.util.StlUtil;
import com.example.three3d.util.WebViewClientUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrinterStartActivity extends AppCompatActivity {

    private static final String TAG = PrinterStartActivity.class.getSimpleName();

    private TextView printerName, status_waiting, textView, textViewTimer;
    ImageView imageView, printingItem;

    private String tempGcode;
    private StlGcode tempStlGcode;

    private Context context;

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
                textView.setText("正在上传...");
            } else if (msg.what == 130) {
                textView.setText("上传完成!");
                tempStlGcode.setFlag(1);
                StlUtil.stlMap.put(tempStlGcode.getRealStlName(), tempStlGcode);
                StlUtil.updateModuleDataBase(context, tempStlGcode.getRealStlName());
                new Thread() {
                    @Override
                    public void run() {
                        // 开始打印
                        String tempCode = tempStlGcode.getLocalGcodeName().substring(tempStlGcode.getLocalGcodeName().lastIndexOf("/") + 1);
                        printNow(tempCode, tempStlGcode);
                    }
                }.start();
            } else if (msg.what == 140) {
                textView.setText(msg.obj.toString());
            }
        }
    };


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.printer_status);
        context = this;


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();

        if (beforePrinter()) {

        } else {
            printerName.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            // status_waiting.setVisibility(View.INVISIBLE);
            printingItem.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            textViewTimer.setVisibility(View.INVISIBLE);
            status_waiting.setText("获取模型失败,请重试!");
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
    }


    private boolean beforePrinter() {
        boolean isSu = false;
        Intent it = this.getIntent();
        String gcodeName = it.getStringExtra("gcodeName");
        String flag = it.getStringExtra("flag");

        if (gcodeName != null && gcodeName.length() == 0) {
            gcodeName = StlUtil.printer_gcode;
            StlUtil.printer_gcode = null;
        } else {
            StlUtil.printer_gcode = null;
        }

        if (gcodeName != null && gcodeName.length() > 0) {
            tempGcode = gcodeName;
            if ("0".equalsIgnoreCase(flag)) {
                tempStlGcode = StlUtil.localMapStl.get(gcodeName);
            } else if ("1".equalsIgnoreCase(flag)) {
                tempStlGcode = StlUtil.stlDataBaseMap.get(gcodeName);
            }
            if (tempStlGcode != null) {
                isSu = true;
                setInfo();
                if (tempStlGcode.getFlag() == 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            postTo3dPrinter(tempStlGcode);
                        }
                    }.start();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            // 开始打印
                            if ("1".equalsIgnoreCase(flag)) {
                                String tempCode = tempStlGcode.getLocalGcodeName().substring(tempStlGcode.getLocalGcodeName().lastIndexOf("/") + 1);
                                printNow(tempCode, tempStlGcode);
                            } else {
                                printNow(tempGcode, tempStlGcode);
                            }
                        }
                    }.start();
                }
            } else {
                System.err.println("获取模型数据失败");
            }
        } else {
            System.err.println("获取文件失败");
        }
        return isSu;
    }

    /**
     * 设置基本信息
     */
    private void setInfo() {
        // 显示基本信息

        printerName.setText(tempStlGcode.getSourceStlName());
        // imageView.setImageURI(Uri.parse(stlGcode.getLocalImg()));

        InputStream is = null;
        try {
            if (tempStlGcode.getFlag() > 0) {
                // InputStream abpath = getClass().getResourceAsStream("/assets/文件名");
                is = getAssets().open(tempStlGcode.getLocalImg().replace("file:///android_asset/", ""));
            } else {
                is = new FileInputStream(tempStlGcode.getLocalImg());
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            printingItem.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeAll(is, null, null);
        }

        status_waiting.setText("正在打印中...");
        textView.setText("0%");
        textViewTimer.setText("剩余: 00:00:00");
    }


    // okhttps 上传文件到3d打印机
    public boolean postTo3dPrinter(StlGcode stlGcode) {
        boolean isSu = false;
        if (PermissionCheckUtil.isReadPermissions) {
            if (stlGcode != null && StlUtil.ESP_8266_URL != null && StlUtil.ESP_8266_URL.length() > 0) {

                File file = new File(stlGcode.getLocalGcodeName());
                if (file.exists() && file.isFile()) {
                    Request request = OkHttpUtil.getRequestByBody(file, StlUtil.getPostFileUrl());
                    sendMessage(120, "开始上传");
                    OkHttpClient client = OkHttpUtil.getClient();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            isSu = true;
                            sendMessage(130, "上传成功");
                            System.err.println("postTo3dPrinter,rs:" + response.body().string());
                        } else {
                            sendMessage(140, "上传文件失败,请重试");
                            System.err.println("postTo3dPrinter,rs: error !!!" + response.body().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "postTo3dPrinter,error:", e);
                        sendMessage(140, "上传文件失败,请重试");
                    }
                } else {
                    sendMessage(140, "获取文件失败");
                }
            } else {
                sendMessage(140, "获取打印机连接失败");
            }
        }
        return isSu;
    }


    private void sendMessage(int what, String msg) {
        Message message = new Message();
        message.what = what;
        message.obj = msg;
        mainHandler.sendMessage(message);
    }

    private void printNow(String gcodeName, StlGcode stlGcode) {
        String url = StlUtil.getPrinterCommond(gcodeName);

        System.err.println("print now....................");
        OkHttpClient client = OkHttpUtil.getClient();
        Request request = OkHttpUtil.getRequest(url);
        System.err.println(url);
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String rs = response.body().string();
                setTextShow(stlGcode.getExeTime() + 120 * StlUtil.SECOND_TIME);
                System.err.println(rs);
            } else {
                System.err.println(gcodeName + ", print error!!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "printNow " + url + "],error:", e);
        }

    }


    private void setTextShow(long count) {
        long oldCount = count;


        StringBuffer timeBf;
        while (count >= 0) {
            try {
                Thread.sleep(1000);
                timeBf = new StringBuffer("剩余: " + IOUtil.getTimeStr(count));
                Message message = new Message();
                message.what = 100 - (int) (count * 100 / oldCount);
                message.obj = timeBf.toString();
                mainHandler.sendMessage(message);
                count -= StlUtil.SECOND_TIME;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
