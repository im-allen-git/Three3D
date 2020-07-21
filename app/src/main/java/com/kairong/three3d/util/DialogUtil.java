package com.kairong.three3d.util;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {


    public static AlertDialog showUpload(Activity activity, String message) {

        AlertDialog mResultDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
        mResultDialog.setCanceledOnTouchOutside(false);

        return mResultDialog;
    }

}
