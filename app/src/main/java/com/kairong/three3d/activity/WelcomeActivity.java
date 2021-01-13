package com.kairong.three3d.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.kairong.three3d.IndexHtmlActivity;
import com.kairong.three3d.R;
import com.kairong.three3d.util.ActivityCollector;
import com.kairong.three3d.util.CacheUtil;
import com.kairong.three3d.config.HtmlConfig;
import com.kairong.three3d.util.PermissionCheckUtil;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ActivityCollector.addActivity(this);

        setContentView(R.layout.welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        PermissionCheckUtil.checkIsPermission(this, this);

        if (PermissionCheckUtil.isReadPermissions && PermissionCheckUtil.isWritePermissions) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);

                        String firstCome = CacheUtil.getSettingNote(context, HtmlConfig.FLAG_JSON, HtmlConfig.WELCOME_FLAG);
                        if (firstCome != null && firstCome.length() > 0) {
                            Intent it = new Intent(context, IndexHtmlActivity.class);
                            //Intent it = new Intent(context, LoginActivity.class);
                            context.startActivity(it);
                        } else {
                            Intent it = new Intent(context, WelcomeSlideActivity.class);
                            context.startActivity(it);
                        }
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            ActivityCollector.finishAll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
