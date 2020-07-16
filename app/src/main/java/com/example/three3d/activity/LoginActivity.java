package com.example.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.R;
import com.example.three3d.util.PermissionCheckUtil;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;

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

        TextView textView = findViewById(R.id.phoneNumber);

        ImageButton weChatBtn = findViewById(R.id.weChatBtn);

        ImageButton aliPayBtn = findViewById(R.id.aliPayBtn);


        if (PermissionCheckUtil.isPhonePermissions) {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String line1Number = telephonyManager.getLine1Number();
            if (!TextUtils.isEmpty(line1Number)) {
                textView.setText(line1Number);
            }
        }
    }
}
