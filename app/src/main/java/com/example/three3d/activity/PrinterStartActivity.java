package com.example.three3d.activity;

import android.annotation.SuppressLint;
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
import com.example.three3d.util.StlUtil;
import com.example.three3d.util.WebViewClientUtil;

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

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what < 100) {
                textView.setText(msg.what + "%");
                textViewTimer.setText(msg.obj.toString());
            } else if (msg.what >= 100) {
                textView.setText("打印完成!");
                textViewTimer.setText(msg.obj.toString());
            }
        }
    };

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.printer_status);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();

        if (setInfo()) {
            new Thread() {
                @Override
                public void run() {
                    // 开始打印
                    printNow(tempGcode, tempStlGcode);
                }
            }.start();
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

    private boolean setInfo() {
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

        StlGcode stlGcode;
        if (gcodeName != null && gcodeName.length() > 0) {

            if ("0".equalsIgnoreCase(flag)) {
                stlGcode = StlUtil.localMapStl.get(gcodeName);
            } else if ("1".equalsIgnoreCase(flag)) {
                stlGcode = StlUtil.stlDataBaseMap.get(gcodeName);
            } else {
                return isSu;
            }
            if (stlGcode != null) {
                // 显示基本信息
                printerName.setText(stlGcode.getSourceStlName());
//                imageView.setImageURI(Uri.parse(stlGcode.getLocalImg()));

                InputStream is = null;
                try {
                    // InputStream abpath = getClass().getResourceAsStream("/assets/文件名");

                    is = getAssets().open(stlGcode.getLocalImg().replace("file:///android_asset/", ""));
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

                tempStlGcode = stlGcode;
                tempGcode = gcodeName;
                isSu = true;
            }
        }
        return isSu;
    }


    private void printNow(String gcodeName, StlGcode stlGcode) {
        // http://10.0.0.63/command_silent?commandText=M23%20/HELLO_~1.GCO%0AM24&PAGEID=0
        String tempGcodeNameStr = gcodeName.substring(0, gcodeName.lastIndexOf("."));
        if (tempGcodeNameStr.length() > 8) {
            tempGcodeNameStr = gcodeName.substring(0, 5) + "_~1" + gcodeName.substring(gcodeName.lastIndexOf("."));
        } else {
            tempGcodeNameStr = gcodeName;
        }


        String url = StlUtil.ESP_8266_URL + "command_silent?commandText=M23%20/" + tempGcodeNameStr.toUpperCase() + "%0AM24&PAGEID=0";

        OkHttpClient client = OkHttpUtil.getClient();
        Request request = OkHttpUtil.getRequest(url);
        System.err.println(url);
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String rs = response.body().string();
                setTextShow(stlGcode.getExeTime() + 60 * StlUtil.SECOND_TIME);
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
