package com.kairong.three3d.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.AuthTask;
import com.kairong.three3d.R;
import com.kairong.three3d.alipay.AuthResult;
import com.kairong.three3d.config.AliPayConfig;
import com.kairong.three3d.config.WXConfig;
import com.kairong.three3d.util.OkHttpUtil;
import com.kairong.three3d.util.PermissionCheckUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TelephonyManager telephonyManager;
    private IWXAPI iwxapi;

    private Context context;

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AliPayConfig.AUTH_SUC_CODE:
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
                case AliPayConfig.AUTH_ERR_CODE:
                    Toast.makeText(context, String.format("授权失败_authCode:%s", "404"), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint({"SourceLockedOrientationActivity", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();// 隐藏标题栏
        //设置Activity竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        PermissionCheckUtil.checkReadyPhone(this, this);

        iwxapi = WXAPIFactory.createWXAPI(this, WXConfig.APP_ID, false);
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

        aliPayBtn.setOnClickListener(view -> aliAuth());

        iwxapi.registerApp(WXConfig.APP_ID);
        weChatBtn.setOnClickListener(v -> {
            if (!iwxapi.isWXAppInstalled()) {
                Toast.makeText(LoginActivity.this, "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show();
            } else {
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                iwxapi.sendReq(req);
            }
        });

    }


    private void aliAuth() {
        // 对授权接口的调用需要异步进行。

        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {
                String authInfo = null;
                boolean isSu = false;
                try {
                    OkHttpClient client = OkHttpUtil.getClient();
                    Map<String, String> paramMap = new HashMap<>();

                    Request request = OkHttpUtil.getPostRequest(AliPayConfig.ALI_PAY_AUTH_INFO, paramMap);
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                        if (null != jsonObject && 200 == jsonObject.getIntValue("code")) {
                            isSu = true;
                            authInfo = jsonObject.getString("data");
                        } else {
                            System.err.println("获取授权失败，请重试!!!");
                            Log.e(TAG, "获取授权失败，请重试!!!");
                        }
                    } else {
                        System.err.println("获取授权失败--网络");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("获取授权失败");
                }

                if (isSu) {
                    String finalAuthInfo = authInfo;

                    // 构造AuthTask 对象
                    AuthTask authTask = new AuthTask(LoginActivity.this);
                    // 调用授权接口
                    // AuthTask#authV2(String info, boolean isShowLoading)，
                    // 获取授权结果。
                    Map<String, String> result = authTask.authV2(finalAuthInfo, true);

                    // 将授权结果以 Message 的形式传递给 App 的其它部分处理。
                    // 对授权结果的处理逻辑可以参考支付宝 SDK Demo 中的实现。
                    Message msg = new Message();
                    msg.what = AliPayConfig.AUTH_SUC_CODE;
                    msg.obj = result;
                    mainHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = AliPayConfig.AUTH_ERR_CODE;
                    msg.obj = "授权远程调用失败";
                    System.err.println("授权远程调用失败");
                    mainHandler.sendMessage(msg);
                }
            }
        };
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }


}
