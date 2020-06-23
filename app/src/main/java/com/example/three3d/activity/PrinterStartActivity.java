package com.example.three3d.activity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.R;

import java.util.Objects;

public class PrinterStartActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.printer_status);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageButton ModuleParamBtn = findViewById(R.id.imageButtonModuleParam);
        ModuleParamBtn.setOnClickListener(v -> {
            actionKey(KeyEvent.KEYCODE_BACK);
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        // this.onCreate(null);
        this.recreate();
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
