package com.example.three3d;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.activity.BulidModuleActivity;
import com.example.three3d.activity.MyAccountActivity;
import com.example.three3d.activity.PrinterActivity;
import com.example.three3d.activity.PrinterStartActivity;
import com.example.three3d.util.PermissionCheckUtil;
import com.example.three3d.util.StlUtil;

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

        PermissionCheckUtil.checkIsPermission(this, this);

        setContentView(R.layout.index);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        initDataBase();
        initView();
    }

    private void initDataBase() {
        StlUtil.getModuleDataBase(this);
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
    }


}
