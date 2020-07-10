package com.example.three3d.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.example.three3d.IndexHtmlActivity;
import com.example.three3d.activity.BulidModuleActivity;
import com.example.three3d.activity.Esp8266Activity;
import com.example.three3d.activity.MyAccountActivity;
import com.example.three3d.activity.PrinterActivity;
import com.example.three3d.activity.PrinterStartActivity;
import com.example.three3d.activity.ShoppingActivity;
import com.example.three3d.activity.UploadGcodeActivity;
import com.example.three3d.pojo.StlGcode;
import com.example.three3d.touchv1.EspTouchActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WebHost {

    List<StlGcode> stlGcodeList = new ArrayList<>();

    public Context context;
    private Handler myHandler;

    private WebView webView;

    // APP缓存路径
    private String filePrePath;

    // 文件全路径
    private String fileAllPath;

    // 保存的stl文件全路径
    private String currentFileName;

    // 保存的img文件全路径
    private String currentImg;

    public String getCurrentFileName() {
        return this.currentFileName;
    }

    public WebHost(Context context, Handler myHandler) {
        this.context = context;
        this.myHandler = myHandler;
        this.filePrePath = context.getApplicationContext().getFilesDir().getAbsolutePath()
                .replace("\\", "/");

    }


    public void setWebView(WebView webView) {
        this.webView = webView;
    }


    @JavascriptInterface
    public boolean saveStl(String fileTxt, String fileName, String imgData) {

        boolean isSu = false;

        isSu = saveImg(imgData);
        if (!isSu) {
            return isSu;
        }

        // setPath();
        String endSuffix = fileName.substring(fileName.lastIndexOf("."));

        String realFileName = fileAllPath + endSuffix;
        File file = new File(realFileName);
        Message msg = new Message();
        if (StlUtil.stlMap.containsKey(fileName) || (file.exists() && !file.isDirectory())) {
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
                    stlGcode.setRealStlName(tempFileAllPath);
                    stlGcode.setSourceStlName(fileName);
                    stlGcode.setSourceZipStlName(tempFileAllPath + ".zip");
                    stlGcode.setCreateTime(StlUtil.getFormatTime(new Date()));
                    stlGcode.setLocalImg(currentImg);
                    StlUtil.stlMap.put(tempFileAllPath, stlGcode);
                    isSu = true;

                    StlUtil.saveModuleDataBase(context, stlGcode);

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
            Intent it = new Intent(this.context.getApplicationContext(), ShoppingActivity.class);
            it.putExtra("url", HtmlUtil.SHOP_HTML);
            this.context.startActivity(it);
        } else if ("3".equalsIgnoreCase(code)) {
            // 模型库首页
            Intent it = new Intent(this.context.getApplicationContext(), IndexHtmlActivity.class);
            // it.putExtra("url", HtmlUtil.SHOP_HTML);
            this.context.startActivity(it);
        } else if ("4".equalsIgnoreCase(code)) {
            // 创建模型
            Intent it = new Intent(this.context.getApplicationContext(), BulidModuleActivity.class);
            // it.putExtra("url", HtmlUtil.SHOP_HTML);
            this.context.startActivity(it);
        } else if ("5".equalsIgnoreCase(code)) {
            Message message = new Message();
            message.what = 5;
            message.obj = "back";
            myHandler.sendMessage(message);
        } else if ("6".equalsIgnoreCase(code)) {
            // 3d打印机

            Intent it = new Intent(this.context.getApplicationContext(), PrinterActivity.class);
            this.context.startActivity(it);


        } else if ("61".equalsIgnoreCase(code)) {
            // 3d打印机
            if (StlUtil.ESP_8266_URL != null && StlUtil.ESP_8266_URL.length() > 0) {
                Intent it = new Intent(this.context.getApplicationContext(), Esp8266Activity.class);
                it.putExtra("esp8266url", StlUtil.ESP_8266_URL);
                this.context.startActivity(it);
            } else {
                Intent it = new Intent(this.context.getApplicationContext(), EspTouchActivity.class);
                this.context.startActivity(it);
            }


            // 测试文件上传
            /*Intent it = new Intent(this.context.getApplicationContext(), UploadDemo.class);
            this.context.startActivity(it);*/

        } else if ("7".equalsIgnoreCase(code)) {
            // 3d打印机 状态页 status
            Intent it = new Intent(this.context.getApplicationContext(), PrinterStartActivity.class);
            this.context.startActivity(it);
        } else if ("8".equalsIgnoreCase(code)) {
            // 上传gcode文件给打印机sd卡
            Intent it = new Intent(this.context.getApplicationContext(), UploadGcodeActivity.class);
            this.context.startActivity(it);
        }
    }

    @JavascriptInterface
    public void welcomeToIndex() {
        // 模型库首页
        Intent it = new Intent(this.context.getApplicationContext(), IndexHtmlActivity.class);
        this.context.startActivity(it);
        Message message = new Message();
        message.what = 1;
        this.myHandler.sendMessage(message);
    }

    @JavascriptInterface
    public String getStlList() {
        stlGcodeList.clear();
        for (Map.Entry<String, StlGcode> fileEntry : StlUtil.stlDataBaseMap.entrySet()) {
            stlGcodeList.add(fileEntry.getValue());
        }
        return JSONObject.toJSONString(stlGcodeList);
    }


    @JavascriptInterface
    public boolean deleteStl(String fileName) {
        if (StlUtil.stlDataBaseMap.containsKey(fileName)) {
            if (StlUtil.stlDataBaseMap.containsKey(fileName)) {
                StlUtil.deleteModuleDataBase(context, fileName);
                StlUtil.stlDataBaseMap.remove(fileName);
            }

            StlGcode stlGcode = StlUtil.stlMap.get(fileName);
            File tempFile;

            if (stlGcode.getSourceZipStlName() != null && stlGcode.getSourceZipStlName().length() > 0) {
                tempFile = new File(stlGcode.getSourceZipStlName());
                tempFile.deleteOnExit();
            }
            if (stlGcode.getServerZipGcodeName() != null && stlGcode.getServerZipGcodeName().length() > 0) {
                tempFile = new File(stlGcode.getServerZipGcodeName());
                tempFile.deleteOnExit();
            }

            if (stlGcode.getLocalGcodeName() != null && stlGcode.getLocalGcodeName().length() > 0) {
                tempFile = new File(stlGcode.getLocalGcodeName());
                tempFile.deleteOnExit();
            }

            if (stlGcode.getLocalImg() != null && stlGcode.getLocalImg().length() > 0) {
                tempFile = new File(stlGcode.getLocalImg());
                tempFile.deleteOnExit();
            }

            tempFile = new File(fileName);
            tempFile.deleteOnExit();

            StlUtil.stlMap.remove(fileName);
            return true;
        }
        return false;
    }


    @JavascriptInterface
    public String getModuleList() {
        BufferedReader bf = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assets = context.getAssets();
            bf = new BufferedReader(new InputStreamReader(
                    assets.open("static/moduleList.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            Log.e("TAG", "getModuleList: error", e);
        } finally {
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    @JavascriptInterface
    public String getLocalStl() {
        List<StlGcode> localStlList = StlUtil.getLocalStl();
        return localStlList.size() == 0 ? null : JSONObject.toJSONString(localStlList);
    }

    @JavascriptInterface
    public boolean sendGcode(String fileName) {
        Message message = new Message();
        message.what = 11;
        message.obj = fileName;
        this.myHandler.sendMessage(message);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @JavascriptInterface
    public boolean printerGcode(String gcodeName, int flag) {
        // flag  0  原始APP的gcode   1 自己创建的模型
        if (StlUtil.ESP_8266_URL == null || StlUtil.ESP_8266_URL.length() == 0) {
            Intent it = new Intent(this.context.getApplicationContext(), PrinterActivity.class);
            StlUtil.printer_gcode = gcodeName;
            this.context.startActivity(it);
        } else {
            StlUtil.printer_gcode = null;
            Intent it = new Intent(this.context.getApplicationContext(), PrinterStartActivity.class);
            it.putExtra("gcodeName", gcodeName);
            it.putExtra("flag", flag);
            this.context.startActivity(it);
        }
        return true;
    }


    @JavascriptInterface
    public boolean getFlagByJson(String fileName) {
        String firstCome = CacheUtil.getSettingNote(this.context, HtmlUtil.FLAG_JSON, fileName);
        return firstCome != null && firstCome.length() > 0;
    }


    @JavascriptInterface
    public void saveFlagByJson(String fileName) {
        Map<String, String> map = new HashMap<>();
        map.put(fileName, "1");
        CacheUtil.saveSettingNote(this.context, HtmlUtil.FLAG_JSON, map);
    }


    /**
     * 调用js方法上传文件
     *
     * @param filePath
     */
    private boolean uploadGcode(String filePath) {
        boolean isSu = false;
        if (filePath != null && filePath.length() > 0) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                webView.loadUrl("javascript:files_check_if_upload_files(" + filePath + ")");
                isSu = true;
            }
        }
        return isSu;
    }


    private boolean saveImg(String fileTxt) {

        setPath();

        String localImg = fileAllPath + ".png";

        byte[] buffer = Base64.decode(fileTxt, Base64.DEFAULT);
        FileOutputStream out = null;
        try {

            out = new FileOutputStream(localImg);
            out.write(buffer);
            out.flush();
            System.err.println("imgFile:" + localImg + ", success");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File(localImg);
        if (file.exists() && file.isFile()) {
            currentImg = localImg;
            // return samplingRateCompress(localImg, fileAllPath + ".png");
            return true;
        }
        return false;
    }


    /**
     * 5.采样率压缩（设置图片的采样率，降低图片像素）
     *
     * @param filePath
     * @param newFilePath
     */
    private boolean samplingRateCompress(String filePath, String newFilePath) {

        Bitmap orBitmap = BitmapFactory.decodeFile(filePath);
        int width = orBitmap.getWidth();

        // 数值越高，图片像素越低
        int inSampleSize = width / 80;


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        // options.inJustDecodeBounds = true;//为true的时候不会真正加载图片，而是得到图片的宽高信息。
        //采样率
        options.inSampleSize = inSampleSize;


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fos = null;

        File file = new File(newFilePath);
        try {
            Bitmap newBitmap = BitmapFactory.decodeFile(filePath, options);
            // 把压缩后的数据存放到baos中
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.flush();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeAll(null, fos, baos);
        }
        File tempFile = new File(newFilePath);
        if (tempFile.exists() && tempFile.isFile()) {
            currentImg = newFilePath;
            System.err.println("imgFile:" + newFilePath + ", success");
        }
        return tempFile.exists() && tempFile.isFile();
    }


    private void setPath() {
        Random random = new Random();
        int nextInt = random.nextInt(9999);
        fileAllPath = filePrePath + "/" + System.currentTimeMillis() + "_" + nextInt;
    }

    public static void disableLongClick(WebView webView) {
        webView.setLongClickable(true);
        webView.setOnLongClickListener(v -> true);
    }


}