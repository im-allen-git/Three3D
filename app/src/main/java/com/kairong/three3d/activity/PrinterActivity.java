package com.kairong.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kairong.three3d.IndexHtmlActivity;
import com.kairong.three3d.R;
import com.kairong.three3d.touchv1.NetUtils;
import com.kairong.three3d.util.CacheUtil;
import com.kairong.three3d.util.HtmlUtil;
import com.kairong.three3d.util.IOUtil;
import com.kairong.three3d.util.StlUtil;
import com.kairong.three3d.util.WebViewClientUtil;

import java.util.Objects;

public class PrinterActivity extends AppCompatActivity {

    private Context context;
    private WifiManager mWifiManager;

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
            WebViewClientUtil.actionKey(KeyEvent.KEYCODE_BACK);
        });
        // 输入密码页面
        ImageButton connectWifiBtn = findViewById(R.id.imageButtonPrint);
        ImageButton gotoIndex = findViewById(R.id.gotoIndex);
        TextView connected_wifi = findViewById(R.id.connected_wifi);
       // StlUtil.ESP_8266_URL = "http://10.0.0.34/";
        if (StlUtil.ESP_8266_URL != null && StlUtil.ESP_8266_URL.length() > 0) {
            connected_wifi.setText(R.string.printer_statue_conn);
            connected_wifi.setTextColor(Color.GREEN);
            gotoIndex.setVisibility(View.VISIBLE);
            connectWifiBtn.setVisibility(View.GONE);
            gotoIndex.setOnClickListener(v -> {
                Intent it = new Intent(this.context.getApplicationContext(), Esp8266Activity.class);
                this.context.startActivity(it);
            });
        } else {
            connected_wifi.setText(R.string.printer_statue_uncon);
            connected_wifi.setTextColor(Color.RED);
            gotoIndex.setVisibility(View.GONE);
            connectWifiBtn.setVisibility(View.VISIBLE);
            connectWifiBtn.setOnClickListener(v -> {
                Intent it = new Intent(context.getApplicationContext(), WifiPassActivity.class);
                startActivity(it);
                if(StlUtil.ESP_8266_URL!=null){
                    finish();
                }
            });
        }
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        checkWifi();
        TextView wifi_name = findViewById(R.id.apSsidText);
    }

    private void checkWifi() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        boolean connected = NetUtils.isWifiConnected(mWifiManager);
        if (!connected) {
            return;
        }
        String ssid = NetUtils.getSsidString(wifiInfo);
        IOUtil.WIFI_SSID = ssid;
    }

}
