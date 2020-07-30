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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.three3d.IndexHtmlActivity;
import com.example.three3d.activity.BulidModuleActivity;
import com.example.three3d.activity.Esp8266Activity;
import com.example.three3d.activity.MyAccountActivity;
import com.example.three3d.activity.PersonDateActivity;
import com.example.three3d.activity.PrinterActivity;
import com.example.three3d.activity.PrinterStartActivity;
import com.example.three3d.activity.ShoppingActivity;
import com.example.three3d.activity.UploadGcodeActivity;
import com.example.three3d.pojo.BindingUserPojo;
import com.example.three3d.pojo.EquipmentPojo;
import com.example.three3d.pojo.StlGcode;
import com.example.three3d.pojo.UserPojo;
import com.example.three3d.pojo.WeighingdataPojo;
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

    List<StlGcode> localStlList = new ArrayList<>();

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
            Intent it = new Intent(this.context.getApplicationContext(), IndexHtmlActivity.class);
            it.putExtra("url", HtmlUtil.MYMODULE_HTML);
            this.context.startActivity(it);
        } else if ("2".equalsIgnoreCase(code)) {
            // 個人資料部分
            Intent it = new Intent(this.context.getApplicationContext(), PersonDateActivity.class);
            it.putExtra("url", HtmlUtil.PERSON_DATE);
            this.context.startActivity(it);
        }
    }

    @JavascriptInterface
    public String getStlList() {
        stlGcodeList.clear();
        for (Map.Entry<String, StlGcode> fileEntry : StlUtil.stlDataBaseMap.entrySet()) {
            stlGcodeList.add(fileEntry.getValue());
        }
        return stlGcodeList.size() == 0 ? null : JSONObject.toJSONString(stlGcodeList);
    }


    @JavascriptInterface
    public boolean deleteStl(String fileName) {
        if (StlUtil.stlMap.containsKey(fileName)) {
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

        if (localStlList == null || localStlList.size() == 0) {
            localStlList = new ArrayList<>();
            StlGcode kitty = new StlGcode(0, "hello_kitty.stl",
                    "file:///android_asset/models/stl/localModules/hello_kitty.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/hello_kitty.gco", "",
                    "file:///android_asset/models/stl/localModules/hello_kitty.png",
                    "74.01mm", "51.22mm", "100.93mm", "18.20M", "7318cm");
            localStlList.add(kitty);
            StlGcode chamaeleo_t = new StlGcode(0, "chamaeleo_t.stl",
                    "file:///android_asset/models/stl/localModules/chamaeleo_t.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/chamaeleo_t.gco", "",
                    "file:///android_asset/models/stl/localModules/chamaeleo_t.png",
                    "26.15mm", "69.46mm", "17.72mm", "5.33M", "151cm");
            localStlList.add(chamaeleo_t);

            StlGcode hand_ok = new StlGcode(0, "hand_ok.stl",
                    "file:///android_asset/models/stl/localModules/hand_ok.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/hand_ok.gco", "",
                    "file:///android_asset/models/stl/localModules/hand_ok.png",
                    "42.78mm", "57.72mm", "110.44mm", "16.40M", "1348cm");
            localStlList.add(hand_ok);

            StlGcode jet_pack_bunny = new StlGcode(0, "jet_pack_bunny.stl",
                    "file:///android_asset/models/stl/localModules/jet_pack_bunny.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/jet_pack_bunny.gco", "",
                    "file:///android_asset/models/stl/localModules/jet_pack_bunny.png",
                    "130.43mm", "92.01mm", "131.28mm", "48.20M", "7318cm");
            localStlList.add(jet_pack_bunny);

            StlGcode god_of_wealth = new StlGcode(0, "god_of_wealth.stl",
                    "file:///android_asset/models/stl/localModules/god_of_wealth.stl", "",
                    "",
                    "file:///android_asset/models/stl/localModules/god_of_wealth.gco", "",
                    "file:///android_asset/models/stl/localModules/god_of_wealth.png",
                    "62.85mm", "57.72mm", "64.23mm", "23.40M", "1945cm");
            localStlList.add(god_of_wealth);

        }

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
    public String printerGcode() {
        return null;
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

    @JavascriptInterface
    // 保存注册用户数据
    public boolean registerOrLogin(String nickName, String mobile) {

        boolean isSu = false;

        int userId = StlUtil.checkUserIdExist(context,mobile);
        if(userId ==0){
            UserPojo userPojo = new UserPojo();
            userPojo.setNickName(nickName);
            userPojo.setMobile(mobile);

            // 保存注册用户数据
            userId = (int)StlUtil.saveUserDataBase(context, userPojo);
//            获取最新插入数据的自增长主键ID
//            userId = StlUtil.getLastInsertRowid(context);
        }

        Map<String, String> stlMap = new HashMap<>();
        stlMap.put("userId", String.valueOf(userId));
        // userId保存类似于session
        CacheUtil.saveSettingNote(context, HtmlUtil.USER_JSON, stlMap);

        isSu = true;

        return isSu;
    }

    @JavascriptInterface
    public boolean getFlagByJson(String fileName) {
        String firstCome = CacheUtil.getSettingNote(this.context, HtmlUtil.USER_JSON, fileName);
        return firstCome != null && firstCome.length() > 0;
    }

    @JavascriptInterface
    // 取得session，userId
    public int getUserId(String fileName) {
        String userId = CacheUtil.getSettingNote(this.context, HtmlUtil.USER_JSON, fileName);
        return Integer.valueOf(userId);
    }



    @JavascriptInterface
    // 查询用户信息数据
    public String  getUserInfoDataList(String userId) {

        // 查询用户信息数据
        List<Map<String, Object>>   userInfoList = StlUtil.getUserInfoData(context, userId);

        return JSON.toJSONString(userInfoList);
    }


    @JavascriptInterface
    // 用户数据修改
    public boolean updateUserInfo(String userId,String nickName,String sex,String birthday,String height,String weight,String wasteRate,String number) {

        boolean isSu = false;

        UserPojo userPojo = new UserPojo();
        userPojo.setUserId(userId);
        userPojo.setNickName(nickName);
        userPojo.setSex(sex);
        userPojo.setBirthday(birthday);
        userPojo.setHeight(height);
        userPojo.setWeight(weight);
        userPojo.setWasteRate(wasteRate);
        userPojo.setNumber(number);



        isSu = true;

        // 用户数据修改
        StlUtil.updateUserInfoDataBase(context, userPojo);

        return isSu;
    }


    @JavascriptInterface
    // 保存群组共享用户数据
    public boolean bindingUserAdd(String userId, String bindingId) {

        boolean isSu = false;

        // 检查绑定用户是否存在
        if(StlUtil.checkbingIdExist(context,userId,bindingId)==0){
            BindingUserPojo bindingUserPojo = new BindingUserPojo();
            bindingUserPojo.setUserId(userId);
            bindingUserPojo.setBindingUserid(bindingId);
            // 保存群组共享用户数据
            StlUtil.saveBindingUserDataBase(context, bindingUserPojo);
            isSu = true;
        }else{
            isSu = false;
        }
        return isSu;
    }

    @JavascriptInterface
    // 删除群组共享用户数据
    public boolean bindingUserDel(String userId,String bingId) {
        boolean isSu = false;
        // 删除群组共享用户数据
        StlUtil.deleteBindingUserDataBase(context, userId, bingId);
        isSu = true;

        return isSu;
    }


    @JavascriptInterface
    // 查询绑定用户信息数据
    public String  getBindingUserList(String userId) {

        // 查询用户信息数据
        List<Map<String, Object>>   bindingUserList = StlUtil.getBindingUserList(context, userId);

        return JSON.toJSONString(bindingUserList);
    }

    @JavascriptInterface
    // 保存设备信息数据
    public boolean equipmentAdd(String mac,String uuId, String name, String userId, String item, String unit
            , String target,String ipAddress,String onlineType) {

        boolean isSu = false;

        EquipmentPojo equipmentPojo = new EquipmentPojo();
        equipmentPojo.setMac(mac);
        equipmentPojo.setUuId(uuId);
        equipmentPojo.setName(name);
        equipmentPojo.setUserId(userId);
        equipmentPojo.setItem(item);
        equipmentPojo.setUnit(unit);
        equipmentPojo.setTarget(target);
        equipmentPojo.setIpAddress(ipAddress);
        equipmentPojo.setOnlineType(onlineType);

        isSu = true;

        // 保存设备信息数据
        StlUtil.saveEquipmentDataBase(context, equipmentPojo);

        return isSu;
    }

    @JavascriptInterface
    // 设备信息删除
    public boolean equipmentDel(String uuID) {
        boolean isSu = false;
        // 设备信息删除
        StlUtil.deleteEquipmentDataBase(context, uuID);
        isSu = true;

        return isSu;
    }

    @JavascriptInterface
    // 设备信息更新
    public boolean updateEquipment(String uuId, String userId,String item,String unit,String target) {

        boolean isSu = false;

        EquipmentPojo equipmentPojo = new EquipmentPojo();
        equipmentPojo.setUuId(uuId);
        equipmentPojo.setUserId(userId);
        equipmentPojo.setItem(item);
        equipmentPojo.setUnit(unit);
        equipmentPojo.setTarget(target);

        isSu = true;

        // 设备信息更新
        StlUtil.updateEquipment(context, equipmentPojo);

        return isSu;
    }



    @JavascriptInterface
    // 获取用户设备状态：蓝牙 wifi
    public String  getEquipmentOnlineType(String userId) {

        String onlineType="";

        // 获取用户设备状态：蓝牙 wifi
        onlineType = StlUtil.getEquipmentOnlineType(context, userId);

        return onlineType;
    }


    @JavascriptInterface
    // 设备信息修改蓝牙wifi状态
    public boolean updateEquipmentOnlineType(String userId,String onlineType) {

        boolean isSu = false;

        EquipmentPojo equipmentPojo = new EquipmentPojo();
        equipmentPojo.setUserId(userId);
        equipmentPojo.setOnlineType(onlineType);

        isSu = true;

        // 设备信息更新
        StlUtil.updateEquipmentOnlineType(context, equipmentPojo);

        return isSu;
    }



    @JavascriptInterface
    // 查询设备信息数据
    public String  getEquipmentDataList(String userId) {

        // 查询设备信息数据
        List<Map<String, Object>>   equipmentDataList = StlUtil.getEquipmentData(context, userId);

        return JSON.toJSONString(equipmentDataList);
    }



    @JavascriptInterface
    // 保存称重信息数据 手动输入
    public boolean weighingdataAdd(String userId,String mac,  String item, String type, String weight
            , String createTime) {

        boolean isSu = false;

        WeighingdataPojo weighingdataPojo = new WeighingdataPojo();
        weighingdataPojo.setUserId(userId);
        weighingdataPojo.setMac(mac);
        weighingdataPojo.setItem(item);
        weighingdataPojo.setType(type);
        weighingdataPojo.setWeight(weight);
        weighingdataPojo.setCreateTime(createTime);

        isSu = true;

        // 保存设备信息数据
        StlUtil.saveWeighingDataBase(context, weighingdataPojo);

        return isSu;
    }

    @JavascriptInterface
    // 更新称重信息数据
    public boolean updateWeighingdata(String type,String number, String wasteRate,String weightStr) {

        boolean isSu = false;

        WeighingdataPojo weighingdataPojo = new WeighingdataPojo();
        weighingdataPojo.setType(type);
        weighingdataPojo.setNumber(number);
        weighingdataPojo.setWasteRate(wasteRate);
        weighingdataPojo.setWeightStr(weightStr);


        isSu = true;

        // 更新称重信息数据
        StlUtil.updateWeighingData(context, weighingdataPojo);

        return isSu;
    }

    @JavascriptInterface
    // 逻辑删除称重信息数据单条数据
    public boolean updateDelWeighingData(String id) {

        boolean isSu = false;

        WeighingdataPojo weighingdataPojo = new WeighingdataPojo();
        weighingdataPojo.setId(Integer.valueOf(id));

        isSu = true;

        // 逻辑删除称重信息数据单条
        StlUtil.updateDelWeighingData(context, weighingdataPojo);

        return isSu;
    }

    @JavascriptInterface
    // 逻辑删除称重信息数据多条
    public boolean updateDelWeighingDataAll(String idAll) {

        boolean isSu = false;

        WeighingdataPojo weighingdataPojo = new WeighingdataPojo();
        weighingdataPojo.setIdAllStr(idAll);

        isSu = true;

        // 逻辑删除称重信息数据
        StlUtil.updateDelWeighingData(context, weighingdataPojo);

        return isSu;
    }

    @JavascriptInterface
    // 查询称重信息数据
    public String  getWeightingDataList(String userId) {

        // 查询称重信息数据
        List<Map<String, Object>>   weightingDataList = StlUtil.getWeightingData(context, userId);

        return JSON.toJSONString(weightingDataList);
    }



    // 数据同步
    // 查询用户信息数据
    public List<UserPojo>   getUserListSync(String userId) {

        // 查询用户信息数据
        List<UserPojo>  userListSync = StlUtil.getUserListSync(context,userId);

        return userListSync;
    }

}