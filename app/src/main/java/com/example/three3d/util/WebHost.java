package com.example.three3d.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

import androidx.annotation.RequiresApi;

import com.example.three3d.IndexActivity;
import com.example.three3d.activity.BulidModuleActivity;
import com.example.three3d.activity.MyAccountActivity;
import com.example.three3d.pojo.StlGcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class WebHost {

    public Context context;
    private Handler myHandler;

    private String filePrePath;

    private String currentFileName;

    public String getCurrentFileName() {
        return this.currentFileName;
    }

    public WebHost(Context context, Handler myHandler) {
        this.context = context;
        this.myHandler = myHandler;
        this.filePrePath = context.getApplicationContext().getFilesDir().getAbsolutePath()
                .replace("\\", "/");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @JavascriptInterface
    public boolean saveStl(String fileTxt, String fileName) {

        boolean isSu = false;

        Random random = new Random();
        int nextInt = random.nextInt(9999);

        String endSuffix = fileName.substring(fileName.lastIndexOf("."));

        String realFileName = filePrePath + "/" + System.currentTimeMillis() + "_" + nextInt + endSuffix;
        File file = new File(realFileName);
        Message msg = new Message();
        if (file.exists() && !file.isDirectory()) {
            System.err.println("已经存在此文件");
            msg.what = 10;
            msg.obj = fileName + ",已经存在此文件";
        } else {
            if (file.getParentFile().exists() && !file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }
            saveFile(fileTxt, file.getAbsolutePath());

            File tempFile = new File(file.getAbsolutePath());


            if (tempFile.exists() && !tempFile.isDirectory()) {
                String tempFileAllPath = tempFile.getAbsolutePath().replace("\\", "/");
                ZipFileUtil.ZipFolder(tempFileAllPath, tempFileAllPath + ".zip");

                File zipFile = new File(tempFileAllPath + ".zip");
                if (zipFile.exists() && zipFile.isFile()) {
                    this.currentFileName = tempFileAllPath;

                    StlGcode stlGcode = new StlGcode();
                    stlGcode.setRealStlFile(tempFileAllPath);
                    stlGcode.setSourceStlFile(fileName);
                    stlGcode.setSrStlZipFile(tempFileAllPath + ".zip");
                    stlGcode.setCreateTime(new Date().toString());
                    StlUtil.stlMap.put(tempFileAllPath, stlGcode);
                    isSu = true;
                    msg.what = 1;
                    msg.obj = fileName;
                    System.err.println("zipfile:" + file.getAbsolutePath() + ", success");
                } else {
                    msg.what = 0;
                    msg.obj = fileName + ",zip error";
                    this.myHandler.sendMessage(msg);
                    System.err.println("zipfile:" + zipFile.getAbsolutePath() + ", error");
                }
            } else {
                msg.what = 0;
                msg.obj = fileName + ",save error";
                System.err.println("tempFile:" + file.getAbsolutePath() + ", error !!");
            }
        }
        this.myHandler.sendMessage(msg);
        return isSu;
    }


    public static boolean saveFile(String fileTxt, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(fileTxt.getBytes());
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    @JavascriptInterface
    public void changeActive(String code) {
        if ("1".equalsIgnoreCase(code)) {
            // 我的模型
            Intent it = new Intent(this.context.getApplicationContext(), MyAccountActivity.class);
            it.putExtra("url", HtmlUtil.MYMODULE_HTML);
            this.context.startActivity(it);
        } else if ("2".equalsIgnoreCase(code)) {
            // 购物商城
            Intent it = new Intent(this.context.getApplicationContext(), MyAccountActivity.class);
            it.putExtra("url", HtmlUtil.SHOP_HTML);
            this.context.startActivity(it);
        } else if ("3".equalsIgnoreCase(code)) {
            // 模型库首页
            Intent it = new Intent(this.context.getApplicationContext(), IndexActivity.class);
            // it.putExtra("url", HtmlUtil.SHOP_HTML);
            this.context.startActivity(it);
        } else if ("4".equalsIgnoreCase(code)) {
            // 创建模型
            Intent it = new Intent(this.context.getApplicationContext(), BulidModuleActivity.class);
            // it.putExtra("url", HtmlUtil.SHOP_HTML);
            this.context.startActivity(it);
        }
    }
}