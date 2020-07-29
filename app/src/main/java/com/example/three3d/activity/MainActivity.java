package com.example.three3d.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.format.Formatter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.three3d.R;

import java.net.NetworkInterface;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取 textView 组件，把结果显示在UI上
        tv = findViewById(R.id.tv);
        tv.setText("ip "+getWifiIp() + " " +getWiFiName()+ " " +getWifiMacAddress());
    }


    /*
     * 获取 WIFI 的名称
     * */
    public String getWiFiName() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo winfo = wm.getConnectionInfo();
            if (winfo != null) {
                String s = winfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "Wifi 未获取到";
    }


    /*
     * 获取 WiFi 的 IP 地址
     * */
    public  String getWifiIp() {
        Context myContext = getApplicationContext();
        if (myContext == null) {
            throw new NullPointerException("上下文 context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (isWifiEnabled()) {
            int ipAsInt = wifiMgr.getConnectionInfo().getIpAddress();
            String ip = Formatter.formatIpAddress(ipAsInt);
            if (ipAsInt == 0) {
                return "未能获取到IP地址";
            } else {
                return ip;
            }
        } else {
            return "WiFi 未连接";
        }
    }

    /*
     * 判断当前 WIFI 是否连接
     * */
    public  boolean isWifiEnabled() {
        Context myContext = getApplicationContext();
        if (myContext == null) {
            throw new NullPointerException("上下文 context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }

    /*
     * 获取 WiFi 的 Mac 地址
     *
     * */
    public String getWifiMacAddress(){

        Context myContext = getApplicationContext();
        if (myContext == null) {
            throw new NullPointerException("上下文 context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (isWifiEnabled()) {

            // 该接口只能获得 02:00:00:00:00:02
            //String macAddress = wifiMgr.getConnectionInfo().getMacAddress();

            String macAddress = null;
            StringBuffer buf = new StringBuffer();
            NetworkInterface networkInterface = null;
            try {
                networkInterface = NetworkInterface.getByName("eth1");
                if (networkInterface == null) {
                    networkInterface = NetworkInterface.getByName("wlan0");
                }
                if (networkInterface == null) {
                    return "02:00:00:00:00:02";
                }
                byte[] addr = networkInterface.getHardwareAddress();


                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                macAddress = buf.toString();
            } catch (SocketException e) {
                e.printStackTrace();
                return "02:00:00:00:00:02";
            }

            return macAddress;


        } else {
            return "WiFi 未连接";
        }
    }




}