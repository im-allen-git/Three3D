package com.kairong.magicBox.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionCheckUtil {

    public static final int WRITE_REQ = 1001;
    public static final int READ_REQ = 1002;
    public static final int PHONE_REQ = 100;
    public static boolean isReadPermissions = false;
    public static boolean isWritePermissions = false;
    public static final int REQUEST_PERMISSION = 0x01;

    public static boolean isPhonePermissions = false;

    /**
     * 检查读取和写入权限
     */
    public static void checkIsPermission(Context context, Activity activity) {
        //CameraDemoActivity 是activity的名字
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //有权限的情况
            isWritePermissions = true;
        } else {
            //没有权限，进行权限申请
            //REQ是本次请求的辨认编号,即 requestCode
            isWritePermissions = false;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQ);
        }

        //CameraDemoActivity 是activity的名字
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //有权限的情况
            isReadPermissions = true;
        } else {
            //没有权限，进行权限申请
            //REQ是本次请求的辨认编号,即 requestCode
            isReadPermissions = false;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQ);
        }
    }


    public static void checkReadyPhone(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            isPhonePermissions = false;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_REQ);
        } else {
            isPhonePermissions = true;
        }
    }



    /**
     * check current sdk if >= 23
     *
     * @return true is need requestPermission
     */
    public static boolean isNeedRequestPermission() {
        return Build.VERSION.SDK_INT >= /*Build.VERSION_CODES.M*/23;
    }

    /**
     * @param context
     * @param permission {@link Manifest.permission} or {@link android.Manifest.permission_group}
     * @return false need request Permission
     */
    public static boolean checkSelfPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, context.getPackageName()));
//        int result = ContextCompat.checkSelfPermission(context, permission);
//        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request Some ManiFest.Permission </br>
     * use this method you need override {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     *
     * @param activity
     * @param permissions {@link Manifest.permission} or {@link android.Manifest.permission_group}
     * @param requestCode can yo do some for onAactivityResult
     */
    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * user deny and never ask again</br>
     * 当用户勾选了申请权限时不再显示，并且拒绝授权时 ，调用该方法检测，返回false 则用户不授予权限，需要弹窗告知用户需要权限的理由，并让其前往系统设置
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean shouldShowRequestPermissiomRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 请求权限
     *
     * @param activity
     * @param permission
     * @param
     * @return true 不需要申请权限  , false 需要申请权限后在操作
     * @CreateData
     */
    public static boolean easyRequestPermission(Activity activity, String permission, int requestCode) {
        if (isNeedRequestPermission()) {
            if (!checkSelfPermission(activity, permission)) {
                requestPermission(activity, new String[]{permission}, requestCode);
                return false;
            }
        }
        return true;
    }
}
