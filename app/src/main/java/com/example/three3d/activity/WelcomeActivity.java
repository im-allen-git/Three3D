package com.example.three3d.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.R;
import com.example.three3d.util.HtmlUtil;
import com.example.three3d.util.StlUtil;
import com.example.three3d.util.WebHost;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context= this;

        setContentView(R.layout.welcome);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent it = new Intent(context, WelcomeSlideActivity.class);
                    context.startActivity(it);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
