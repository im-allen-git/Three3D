package com.kairong.magicBox.config;

/**
 * 支付宝相关参数
 */
public class AliPayConfig {


    public static final String ALI_PAY_ORDER_INFO = "https://192.168.1.67:448/aliPay/getOrderInfo";
    public static final String ALI_PAY_AUTH_INFO = "https://192.168.1.67:448/aliPay/getAuthInfo";

    // 授权成功
    public static final int AUTH_SUC_CODE = 99;
    // 授权失败
    public static final int AUTH_ERR_CODE = 44;

    // 支付成功
    public static final int PAY_SUC_CODE = 1688;
    // 支付失败
    public static final int PAY_ERR_CODE = 1644;


    public static final String API_NAME = "com.alipay.account.auth";
    public static final String APP_ID = "2021001180691205";
    public static final String APP_NAME = "MagicBoxKR";
    public static final String AUTH_TYPE = "AUTHACCOUNT";
    public static final String BIZ_TYPE = "openservice";
    public static final String METHOD = "alipay.open.auth.sdk.code.get";
    public static final String PID = "";
    public static final String PRODUCT_ID = "APP_FAST_LOGIN";
    public static final String SCOPE = "kuaijie";

    public static final String SIGN_TYPE = "RSA2";
    public static final String TARGET_ID = "";
    public static final String SIGN = "";

    private static final String authInfo = "apiname=%s&app_id=%s&app_name=%s&auth_type=%s&biz_type=%s&method=%s&pid=%s&product_id=%s&scope=%s&sign_type=%s&target_id=%s&sign=%s";


    public static String getAuthInfo() {
        return String.format(authInfo, API_NAME, APP_ID, APP_NAME, AUTH_TYPE, BIZ_TYPE, METHOD, PID, PRODUCT_ID, SCOPE, SIGN_TYPE, TARGET_ID, SIGN);
    }

}
