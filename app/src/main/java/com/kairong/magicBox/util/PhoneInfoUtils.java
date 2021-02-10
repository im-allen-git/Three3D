package com.kairong.magicBox.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by administrator on 2018/3/31.
 * 获取SIM卡信息和手机号码
 */

public class PhoneInfoUtils {

    private static String TAG = "PhoneInfoUtils";

    private TelephonyManager telephonyManager;
    private Context context;

    public PhoneInfoUtils(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    //获取sim卡iccid
    public String getIccid() {
        String iccid = "N/A";
        iccid = telephonyManager.getSimSerialNumber();
        return iccid;
    }

    //获取电话号码
    @SuppressLint("MissingPermission")
    public String getNativePhoneNumber() {
        return telephonyManager.getLine1Number();
    }

    //获取手机服务商信息
    public String getProvidersName() {
        String providersName = "N/A";
        //移动运营商编号
        String networkOperator = telephonyManager.getNetworkOperator();
        //IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
//        Flog.d(TAG,"NetworkOperator=" + NetworkOperator);
        if (networkOperator.equals("46000") || networkOperator.equals("46002")) {
            providersName = "中国移动";//中国移动
        } else if (networkOperator.equals("46001")) {
            providersName = "中国联通";//中国联通
        } else if (networkOperator.equals("46003")) {
            providersName = "中国电信";//中国电信
        }
        return providersName;

    }

    @SuppressLint("MissingPermission")
    public String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuffer sb = new StringBuffer();
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());//移动运营商编号
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());//移动运营商名称
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        return sb.toString();
    }
}