package com.kairong.three3d.alipay;

import android.text.TextUtils;

import java.util.Map;

import lombok.Data;

@Data
public class AuthResult {

    private String resultStatus;
    private String result;
    private String memo;
    private String resultCode;
    private String authCode;
    private String alipayOpenId;

    public AuthResult(Map<String, String> rawResult, boolean removeBrackets) {
        if (rawResult == null) {
            return;
        }

        for (String key : rawResult.keySet()) {
            if (TextUtils.equals(key, "resultStatus")) {
                resultStatus = rawResult.get(key);
            } else if (TextUtils.equals(key, "result")) {
                result = rawResult.get(key);
            } else if (TextUtils.equals(key, "memo")) {
                memo = rawResult.get(key);
            }
        }

        String[] resultValue = result.split("&");
        for (String value : resultValue) {
            if (value.startsWith("alipay_open_id")) {
                alipayOpenId = removeBrackets(getValue("alipay_open_id=", value), removeBrackets);
                continue;
            }
            if (value.startsWith("auth_code")) {
                authCode = removeBrackets(getValue("auth_code=", value), removeBrackets);
                continue;
            }
            if (value.startsWith("result_code")) {
                resultCode = removeBrackets(getValue("result_code=", value), removeBrackets);
                continue;
            }
        }

    }

    private String removeBrackets(String str, boolean remove) {
        if (remove) {
            if (!TextUtils.isEmpty(str)) {
                if (str.startsWith("\"")) {
                    str = str.replaceFirst("\"", "");
                }
                if (str.endsWith("\"")) {
                    str = str.substring(0, str.length() - 1);
                }
            }
        }
        return str;
    }


    private String getValue(String header, String data) {
        return data.substring(header.length(), data.length());
    }

}
