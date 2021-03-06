package com.kairong.magicBox.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.kairong.magicBox.R;
import com.kairong.magicBox.util.ActivityCollector;
import com.kairong.magicBox.util.PermissionCheckUtil;
import com.kairong.magicBox.util.WebHost;

import java.util.Objects;

public class AuthorizationActivity extends AppCompatActivity {
    private WebHost webHost;
    private Context context;
    WebView webView;

    private Activity mContext;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        PermissionCheckUtil.checkReadyPhone(this, this);

        context = this;
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
