package com.example.three3d;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.three3d.activity.BulidModuleActivity;
import com.example.three3d.activity.MyAccountActivity;
import com.example.three3d.activity.PrinterActivity;
import com.example.three3d.activity.PrinterStartActivity;

import java.util.Objects;

public class IndexActivity extends AppCompatActivity {

    private Context context;

    private static final int WRITE_REQ = 1001;
    private static final int READ_REQ = 1002;
    private static boolean isReadPermissions = false;
    private static boolean isWritePermissions = false;


    @SuppressLint({"SourceLockedOrientationActivity", "AddJavascriptInterface", "SetJavaScriptEnabled"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIsPermission();

        setContentView(R.layout.index);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        initView();
    }


    private void initView() {
        // 3d打印机
        Button printerLoadBtn = findViewById(R.id.printer_load);
        printerLoadBtn.setOnClickListener(v -> {
            Intent it = new Intent(context.getApplicationContext(), PrinterActivity.class);
            startActivity(it);
        });

        //模型库
        // Button localModuleBtn = findViewById(R.id.local_module);

        // 我的模型
        Button myAccountBtn = findViewById(R.id.my_account);
        myAccountBtn.setOnClickListener(v -> {
            Intent it = new Intent(context.getApplicationContext(), MyAccountActivity.class);
            startActivity(it);
        });

        // 创建模型
        ImageButton newModuleBtn = findViewById(R.id.image_button_new_module);
        newModuleBtn.setOnClickListener(v -> {
            Intent it = new Intent(context.getApplicationContext(), BulidModuleActivity.class);
            startActivity(it);
        });

        // 模型参数
        ImageButton moduleParamBtn = findViewById(R.id.image_button_module_param);
        moduleParamBtn.setOnClickListener(v -> {

        });

        // 链接打印机页面
        ImageButton printBtn = findViewById(R.id.image_button_print);
        printBtn.setOnClickListener(v -> {
            Intent it = new Intent(context.getApplicationContext(), PrinterStartActivity.class);
            startActivity(it);

        });
    }


    /**
     * 检查读取和写入权限
     */
    private void checkIsPermission() {
        //CameraDemoActivity 是activity的名字
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //有权限的情况
            isWritePermissions = true;
        } else {
            //没有权限，进行权限申请
            //REQ是本次请求的辨认编号,即 requestCode
            isWritePermissions = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQ);
        }

        //CameraDemoActivity 是activity的名字
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //有权限的情况
            isReadPermissions = true;
        } else {
            //没有权限，进行权限申请
            //REQ是本次请求的辨认编号,即 requestCode
            isReadPermissions = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQ);
        }
    }


}
