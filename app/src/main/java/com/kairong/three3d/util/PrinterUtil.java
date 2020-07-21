package com.kairong.three3d.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.kairong.three3d.config.PrinterConfig;
import com.kairong.three3d.pojo.StlGcode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrinterUtil {
    private static final String TAG = PrinterUtil.class.getSimpleName();

    public static volatile boolean isRun = true;

    public static volatile StlGcode tempStlGcode = new StlGcode();

    public static volatile MyThread myThread = null;

    /**
     * 检查传递参数
     *
     * @return
     */
    public static boolean beforePrinter(Activity activity) {
        boolean isSu = false;

        if (PrinterConfig.upload_count > 0) {
            DialogUtil.showUpload(activity, "存在上传文件，请排队!");
            return false;
        }
        if (PrinterConfig.printer_count > 0) {
            DialogUtil.showUpload(activity, "存在上传文件，请排队!");
            return false;
        }


        Intent it = activity.getIntent();
        String gcodeName = it.getStringExtra("gcodeName");
        String flag = it.getStringExtra("flag");

        if (gcodeName != null && gcodeName.length() > 0) {
            if ("0".equalsIgnoreCase(flag)) {
                tempStlGcode = StlDealUtil.localMapStl.get(gcodeName);
            } else if ("1".equalsIgnoreCase(flag)) {
                tempStlGcode = StlDealUtil.stlDataBaseMap.get(gcodeName);
            }
            if (tempStlGcode != null) {
                //保存到队列中
                StlDealUtil.setPrinterUploadInfo(tempStlGcode, Integer.parseInt(flag), Integer.parseInt(flag));
                isSu = true;
            } else {
                System.err.println("获取模型数据失败");
            }
        } else {
            System.err.println("获取文件失败");
        }
        return isSu;
    }


    /**
     * 设置基本信息
     */
    public static void setPrinterInfo(Context context, TextView printerName, ImageView imageView, ImageView printingItem,
                                      TextView status_waiting, TextView textView, TextView textViewTimer, StlGcode tempStlGcode) {
        // 显示基本信息

        if (PrinterConfig.currPrinterGcodeInfo != null) {
            tempStlGcode = PrinterConfig.currPrinterGcodeInfo.getStlGcode();
        }

        printerName.setText(tempStlGcode.getSourceStlName());
        // imageView.setImageURI(Uri.parse(stlGcode.getLocalImg()));

        InputStream is = null;
        try {
            System.err.println(tempStlGcode.getLocalImg());
            if (tempStlGcode.getLocalFlag() > 0) {
                // InputStream abpath = getClass().getResourceAsStream("/assets/文件名");
                is = context.getAssets().open(tempStlGcode.getLocalImg().replace("file:///android_asset/", ""));
            } else {
                is = new FileInputStream(tempStlGcode.getLocalImg());
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            printingItem.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeAll(is, null, null);
        }

        status_waiting.setText("正在打印中...");
        if (PrinterConfig.currPrinterGcodeInfo != null) {
            long timeStady = System.currentTimeMillis() - PrinterConfig.currPrinterGcodeInfo.getBegin_time();
            long stadyRate = timeStady * 100 / PrinterConfig.currPrinterGcodeInfo.getStlGcode().getExeTime();
            textView.setText(stadyRate + "%");
            textViewTimer.setText("剩余: " + IOUtil.getTimeStr(PrinterConfig.currPrinterGcodeInfo.getStlGcode().getExeTime() - timeStady));
        } else {
            textView.setText("0%");
            textViewTimer.setText("剩余: 00:00:00");
        }

    }


    public static Thread getShowThread(Handler mainHandler, StlGcode stlGcode) {
        myThread = new MyThread(mainHandler, stlGcode);
        return myThread;
    }


    static class MyThread extends Thread {
        private Handler mainHandler;
        private StlGcode stlGcode;

        public MyThread(Handler mainHandler, StlGcode stlGcode) {
            this.mainHandler = mainHandler;
            this.stlGcode = stlGcode;
        }

        @Override
        public void run() {
            File file = new File(stlGcode.getLocalGcodeName());
            int fileSize = IOUtil.getFileSize(file);
            uploadTextShow(mainHandler, fileSize * 15 / 1024 / 20);
        }
    }

    private static void uploadTextShow(Handler mainHandler, int count) {

        int oldCount = count;
        if (PrinterConfig.currPrinterGcodeInfo.getBegin_time() > 0) {
            count -= (System.currentTimeMillis() - PrinterConfig.currPrinterGcodeInfo.getBegin_time()) / PrinterConfig.SECOND_TIME;
        }
        System.err.println("oldCount/count:" + oldCount + "/" + count);
        StringBuffer timeBf = new StringBuffer("正在上传.");
        while (isRun && count > 1) {
            count--;
            try {
                Thread.sleep(1000);
                timeBf = new StringBuffer("剩余: " + IOUtil.getTimeStr(count * PrinterConfig.SECOND_TIME));
                Message message = new Message();
                if (isRun) {
                    message.what = count * 100 / oldCount;
                    message.obj = timeBf.toString();
                    mainHandler.sendMessage(message);
                } else {
                    //message.what = 150;
                    // mainHandler.sendMessage(message);
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void printNow(String gcodeName, StlGcode stlGcode, Handler mainHandler) {
        String url = getPrinterCommond(gcodeName);

        if (PrinterConfig.is_background == 0) {
            PrinterConfig.currPrinterGcodeInfo.setBegin_time(System.currentTimeMillis());
            PrinterConfig.is_background = 1;
            System.err.println("print now....................");
            OkHttpClient client = OkHttpUtil.getClient();
            Request request = OkHttpUtil.getRequest(url);
            System.err.println(url);
            try {
                setTextShow(stlGcode.getExeTime(), mainHandler);

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String rs = response.body().string();
                    System.err.println(rs);
                    PrinterConfig.printer_count = 0;
                    PrinterConfig.is_background = 0;
                } else {
                    System.err.println(gcodeName + ", print error!!!!");
                    PrinterConfig.printer_count = 0;
                    PrinterConfig.is_background = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "printNow " + url + "],error:", e);
                PrinterConfig.printer_count = 0;
                PrinterConfig.is_background = 0;
            }
        } else {
            setTextShow(stlGcode.getExeTime(), mainHandler);
        }
    }

    private static void setTextShow(long count, Handler mainHandler) {

        long oldCount = count;
        if (PrinterConfig.currPrinterGcodeInfo.getBegin_time() > 0) {
            count -= (System.currentTimeMillis() - PrinterConfig.currPrinterGcodeInfo.getBegin_time()) / PrinterConfig.SECOND_TIME;
        }

        StringBuffer timeBf;
        while (count > 1) {
            try {
                Thread.sleep(1000);
                timeBf = new StringBuffer("剩余: " + IOUtil.getTimeStr(count));
                Message message = new Message();
                message.what = 100 - (int) (count * 100 / oldCount);
                message.obj = timeBf.toString();
                mainHandler.sendMessage(message);
                count -= PrinterConfig.SECOND_TIME;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (count <= 0) {
            PrinterConfig.currPrinterGcodeInfo = null;
            PrinterConfig.printerList.clear();
        }
    }


    /**
     * 获取打印命令
     *
     * @param gcodeName
     * @return
     */
    public static String getPrinterCommond(String gcodeName) {
        // http://10.0.0.63/command_silent?commandText=M23%20/HELLO_~1.GCO%0AM24&PAGEID=0
        String tempGcodeNameStr = gcodeName.substring(0, gcodeName.lastIndexOf("."));
        if (tempGcodeNameStr.length() > 8) {
            tempGcodeNameStr = gcodeName.substring(0, 5) + "_~1" + gcodeName.substring(gcodeName.lastIndexOf("."));
        } else {
            tempGcodeNameStr = gcodeName;
        }
        return PrinterConfig.ESP_8266_URL + "command_silent?commandText=M23%20/" + tempGcodeNameStr.toUpperCase() + "%0AM24&PAGEID=0";
    }


    /**
     * 上传文件命令url
     *
     * @return
     */
    public static String getPostFileUrl() {
        // http://10.0.0.34/upload_serial
        return PrinterConfig.ESP_8266_URL + "upload_serial";
    }

    /**
     * 获取sd卡命令url
     *
     * @return
     */
    public static String getSDListUrl() {
        // http://10.0.0.34/upload_serial
        return PrinterConfig.ESP_8266_URL + "command?commandText=M20&PAGEID=0";
    }

}
