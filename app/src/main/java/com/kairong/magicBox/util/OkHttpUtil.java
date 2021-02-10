package com.kairong.magicBox.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.kairong.magicBox.pojo.HttpsTrustManager;
import com.kairong.magicBox.pojo.ProgressRequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {

    private static final int CONNECT_TIMEOUT = 900;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // 上传stl生成gcode路径
    public static final String FILE_UPLOAD_URL = "https://192.168.1.67:443/file/uploadFileAndGenGcodeAndrioid";
    public static final String FILE_DOWN_URL = "https://192.168.1.67:443/file/downloadFileAndroid?fileName=";


    private static volatile OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory(), new HttpsTrustManager())
            .hostnameVerifier(new HttpsTrustManager.TrustAllHostnameVerifier())
            .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT * 2, TimeUnit.SECONDS).build();


    public static Request getRequestByBody(File file, Map<String, String> commandLineMap, String url) {
        String multipartStr = "multipart/form-data";
        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(multipartStr), file))
                .addFormDataPart("commandLineMap", commandLineMap.toString()).build();
        return new Request.Builder().url(url).post(formBody).build();
    }


    public static Request getRequestByBody(File file, String url) {
        String multipartStr = "multipart/form-data";
        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(multipartStr), file)).build();
        return new Request.Builder().url(url).post(formBody).build();
    }

    public static Request getRequest(String url) {
        //构建一个请求
        return new Request.Builder().addHeader("Connection", "close")
                .addHeader("Accept", "*/*")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                .get().url(url).build();
    }


    @SuppressLint("NewApi")
    public static Request getPostRequest(String url, Map<String, String> paramMap) {

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (paramMap != null && paramMap.size() > 0) {
            paramMap.forEach((k, v) -> bodyBuilder.add(k, v));
        }
        return new Request.Builder()
                .url(url)
                .post(bodyBuilder.build())
                .build();
    }

    public static void writeToFile(Response response, File gcodeFile) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream output = null;

        try {
            inputStream = response.body().byteStream();
            fileOutputStream = new FileOutputStream(gcodeFile);
            output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            output.flush();
            output.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeAll(inputStream, fileOutputStream, output);
        }

    }


    public static OkHttpClient getClient() {
        if (client == null) {
            initClient();
        }
        return client;
    }

    public static OkHttpClient getClient(Handler mainHandler, TextView textView) {
        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originRequest = chain.request();
                Request targetRequest = originRequest.newBuilder()
                        .post(new ProgressRequestBody(originRequest.body(), textView, mainHandler))  //封装上传进度拦截器
                        .build();
                return chain.proceed(targetRequest);
            }
        }).readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT * 2, TimeUnit.SECONDS).build();
        return client;
    }


    private static void initClient() {
        client = new OkHttpClient.Builder()
                .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory(), new HttpsTrustManager())
                .hostnameVerifier(new HttpsTrustManager.TrustAllHostnameVerifier())
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT * 2, TimeUnit.SECONDS).build();
    }

    public static void sendMessage(int what, String msg, Handler mainHandler) {
        Message message = new Message();
        message.what = what;
        message.obj = msg;
        mainHandler.sendMessage(message);
    }
}
