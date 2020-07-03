package com.example.three3d.activity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.R;
import com.example.three3d.util.IOUtil;

import java.util.Objects;

public class PrinterActivity extends AppCompatActivity {

    private Context context;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.connect_printer);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ImageButton ModuleParamBtn = findViewById(R.id.imageButtonModuleParam);
        ModuleParamBtn.setOnClickListener(v -> {
            actionKey(KeyEvent.KEYCODE_BACK);
        });

        // 输入密码页面
        ImageButton connectWifiBtn = findViewById(R.id.imageButtonPrint);
        connectWifiBtn.setOnClickListener(v -> {
            Intent it = new Intent(context.getApplicationContext(), WifiPassActivity.class);
            startActivity(it);
        });

        TextView wifi_name = findViewById(R.id.apSsidText);
        wifi_name.setText(IOUtil.WIFI_SSID);

    }


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
}
