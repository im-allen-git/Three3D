package com.kairong.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.AuthTask;
import com.kairong.three3d.R;
import com.kairong.three3d.alipay.AuthResult;
import com.kairong.three3d.config.AliPayConfig;
import com.kairong.three3d.util.PermissionCheckUtil;

import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;
    private static final int SDK_AUTH_FLAG = 110;

    private Context context;

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_AUTH_FLAG:
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();
                    // 判断resultStatus 为“9000”且result_code为“200”则代表授权成功，
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value传入，则支付账户为该授权账户，这个支付先不做
                        //thirdLogin(authResult.getAuthCode());//开发者自己的方法，把code传给后台同事，他们拿code换token
                    } else {
                        if (TextUtils.isEmpty(authResult.getAuthCode())) {
                            Toast.makeText(context, "授权取消", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(context, String.format("授权失败_authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

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

        aliPayBtn.setOnClickListener(view -> pay());

    }


    private void pay() {

        final String authInfo = AliPayConfig.getAuthInfo();
        // 对授权接口的调用需要异步进行。
        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(LoginActivity.this);
                // 调用授权接口
                // AuthTask#authV2(String info, boolean isShowLoading)，
                // 获取授权结果。
                Map<String, String> result = authTask.authV2(authInfo, true);

                // 将授权结果以 Message 的形式传递给 App 的其它部分处理。
                // 对授权结果的处理逻辑可以参考支付宝 SDK Demo 中的实现。
                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
            }
        };
        Thread authThread = new Thread(authRunnable);
        authThread.start();

    }
}
