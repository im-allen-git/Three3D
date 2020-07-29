package com.example.three3d.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {

    // 上传stl生成gcode路径
    public static final String FILE_UPLOAD_URL = "https://192.168.1.67:448/file/uploadFileAndGenGcode";
    public static final String FILE_DOWN_URL = "https://192.168.1.67:448/file/downloadFile?fileName=";

    // 数据同步服务器连接
    public static final String USER_URL = "https://192.168.1.55:8080/user/userDataSync";


    //创建基本线程池
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 7, 20, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50));


    private static volatile OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory(), new HttpsTrustManager())
            .hostnameVerifier(new HttpsTrustManager.TrustAllHostnameVerifier())
            .readTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(600, TimeUnit.SECONDS)
            .connectTimeout(1200, TimeUnit.SECONDS).build();


    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            initThreadPoolExecutor();
        }
        return threadPoolExecutor;
    }


    private static void initThreadPoolExecutor() {
        threadPoolExecutor = new ThreadPoolExecutor(5, 7, 20, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50));
    }


    public static Request getRequestByBody(File file, Map<String, String> commandLineMap, String url) {
        String multipartStr = "multipart/form-data";
        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(multipartStr), file))
                .addFormDataPart("commandLineMap", commandLineMap.toString()).build();
        return new Request.Builder().url(url).post(formBody).build();
    }


    public static Request getRequest(String url) {
        //构建一个请求
        return new Request.Builder().addHeader("Connection", "close")
                .addHeader("Accept", "*/*")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                .get().url(url).build();
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


    private static void initClient() {
        client = new OkHttpClient.Builder()
                .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory(), new HttpsTrustManager())
                .hostnameVerifier(new HttpsTrustManager.TrustAllHostnameVerifier())
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .connectTimeout(1200, TimeUnit.SECONDS).build();
    }
}
