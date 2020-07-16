package com.example.three3d.util;

import android.os.Environment;
import android.util.Log;

import com.example.three3d.pojo.StlGcode;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class IOUtil {

    /**
     * download路径
     */
    public static final String DOWN_LOAD_PATH = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath();

    public static final String DOWN_CACHE_PATH = Environment.getDownloadCacheDirectory().getAbsolutePath();

    public static volatile String WIFI_SSID = null;


    public static boolean copyFile(String oldPathName, String newPathName) {

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }
            fileInputStream = new FileInputStream(oldFile);
            fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeAll(fileInputStream, fileOutputStream, null);
        }
    }


    public static void getGoceInfo(StlGcode stlGcode) {


        File file = new File(stlGcode.getLocalGcodeName());
        if (file.exists() && file.isFile()) {

            Map<String, Double> gcodeMap = new HashMap<>();
            gcodeMap.put("X", 0D);
            gcodeMap.put("Y", 0D);
            gcodeMap.put("Z", 0D);

            gcodeMap.put("X_M", 0D);
            gcodeMap.put("Y_M", 0D);
            gcodeMap.put("Z_M", 0D);

            gcodeMap.put("size", 0D);
            //fill_density

            gcodeMap.put("fill_density", 0D);
            gcodeMap.put("perimeter_speed", 0D);
            gcodeMap.put("filament_used", 0D);


            InputStream instream = null;
            InputStreamReader inputreader = null;
            BufferedReader buffreader = null;
            try {
                instream = new FileInputStream(file);

                int fileS = instream.available();
                genFileSize(fileS, stlGcode);

                inputreader = new InputStreamReader(instream);
                buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    paraseGcodeLine(line, gcodeMap);
                }
                instream.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                readClose(instream, inputreader, buffreader);
            }

            // 循环完成后，计算长宽高和打印时间

            DecimalFormat df = new DecimalFormat("#.00");

            System.err.println();
            System.err.println("X_M/X:" + gcodeMap.get("X_M") + "/" + gcodeMap.get("X"));
            System.err.println("Y_M/Y:" + gcodeMap.get("Y_M") + "/" + gcodeMap.get("Y"));
            System.err.println("Z_M/Y:" + gcodeMap.get("Z_M") + "/" + gcodeMap.get("Z"));

            stlGcode.setLength("X:" + df.format(gcodeMap.get("X_M") - gcodeMap.get("X")));
            stlGcode.setWidth("Y:" + df.format(gcodeMap.get("Y_M") - gcodeMap.get("Y")));
            stlGcode.setHeight("Z:" + df.format(gcodeMap.get("Z_M") - gcodeMap.get("Z")));
            stlGcode.setMaterial(df.format(gcodeMap.get("filament_used") / 10D) + "cm");

            System.err.println("filament_used:" + gcodeMap.get("filament_used"));
            System.err.println("fill_density:" + gcodeMap.get("fill_density"));
            System.err.println("perimeter_speed:" + gcodeMap.get("perimeter_speed"));

            if (gcodeMap.get("fill_density") > 0 && gcodeMap.get("perimeter_speed") > 0) {
                double exeTime = Math.ceil(gcodeMap.get("filament_used") / gcodeMap.get("perimeter_speed")
                        * gcodeMap.get("fill_density") * StlUtil.MINUTE_TIME
                );
                long tempTime = (long) exeTime * 10;
                stlGcode.setExeTime(tempTime);
                stlGcode.setExeTimeStr(IOUtil.getTimeStr(tempTime));
            }
        }

    }


    /**
     * 获取文件大小
     *
     * @param fileS
     * @param stlGcode
     */
    private static void genFileSize(int fileS, StlGcode stlGcode) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "0";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        stlGcode.setSize(fileSizeString);
    }


    /**
     * gcode行数据
     */
    private static void paraseGcodeLine(String gcodeStr, Map<String, Double> gcodeMap) {
        if (gcodeStr.startsWith("G1")) {
            String[] tempList = gcodeStr.split(" ");
            for (String child : tempList) {
                double tempD = 0;
                if (child.startsWith("X")) {
                    tempD = Double.parseDouble(child.substring(1).trim());
                    if (gcodeMap.get("X_M") < tempD) {
                        gcodeMap.put("X_M", tempD);
                    }
                    if (gcodeMap.get("X") == 0) {
                        gcodeMap.put("X", tempD);
                    } else if (gcodeMap.get("X") > tempD) {
                        gcodeMap.put("X", tempD);
                    }
                } else if (child.startsWith("Y")) {
                    tempD = Double.parseDouble(child.substring(1).trim());
                    if (gcodeMap.get("Y_M") < tempD) {
                        gcodeMap.put("Y_M", tempD);
                    }
                    if (gcodeMap.get("Y") == 0) {
                        gcodeMap.put("Y", tempD);
                    } else if (gcodeMap.get("Y") > tempD) {
                        gcodeMap.put("Y", tempD);
                    }
                } else if (child.startsWith("Z")) {
                    tempD = Double.parseDouble(child.substring(1).trim());
                    if (gcodeMap.get("Z_M") < tempD) {
                        gcodeMap.put("Z_M", tempD);
                    }
                    if (gcodeMap.get("Y") == 0) {
                        gcodeMap.put("Y", tempD);
                    } else if (gcodeMap.get("Y") > tempD) {
                        gcodeMap.put("Y", tempD);
                    }
                }
            }
        } else if (gcodeStr.startsWith("; fill_density")) {
            // 填充率
            String tempList = gcodeStr.substring(gcodeStr.indexOf("=") + 1);
            if (tempList.contains("%")) {
                gcodeMap.put("fill_density", Double.parseDouble(tempList.replace("%", "").trim()) / 100D);
            } else {
                gcodeMap.put("fill_density", Double.parseDouble(tempList.trim()) / 100D);
            }

        } else if (gcodeStr.startsWith("; perimeter_speed")) {
            // 打印速度
            String tempList = gcodeStr.substring(gcodeStr.indexOf("=") + 1);
            if (tempList.contains("mm")) {
                gcodeMap.put("perimeter_speed", Double.parseDouble(tempList.replace("mm", "").trim()));
            } else {
                gcodeMap.put("perimeter_speed", Double.parseDouble(tempList.trim()));
            }
        } else if (gcodeStr.startsWith("; filament used")) {
            String tempList = gcodeStr.substring(gcodeStr.indexOf("=") + 1);
            double tempUsed = 0;
            if (tempList.contains("mm")) {
                tempUsed = Double.parseDouble(tempList.split("mm")[0].trim());
            } else {
                tempUsed = Double.parseDouble(tempList.trim().split(" ")[0].trim());
            }
            gcodeMap.put("filament_used", gcodeMap.get("filament_used") + tempUsed);
        }
    }


    public static void closeAll(InputStream is, FileOutputStream fileOutputStream, ByteArrayOutputStream output) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (output != null) {
            try {
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readClose(InputStream is, InputStreamReader inputreader, BufferedReader buffreader) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputreader != null) {
            try {
                inputreader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (buffreader != null) {
            try {
                buffreader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getTimeStr(long count) {
        long hourTime = 0;
        long minuteTime = 0;
        long secondTime = 0;
        StringBuffer timeBf = new StringBuffer();

        hourTime = count / StlUtil.HOUR_TIME;
        if (hourTime > 0) {
            if (hourTime < 10) {
                timeBf.append("0" + hourTime + ":");
            } else {
                timeBf.append("" + hourTime + ":");
            }
        } else {
            timeBf.append("00:");
        }

        minuteTime = (count - hourTime * StlUtil.HOUR_TIME) / StlUtil.MINUTE_TIME;
        if (minuteTime > 0) {
            if (minuteTime < 10) {
                timeBf.append("0" + minuteTime + ":");
            } else {
                timeBf.append("" + minuteTime + ":");
            }
        } else {
            timeBf.append("00:");
        }

        secondTime = (count - hourTime * StlUtil.HOUR_TIME - minuteTime * StlUtil.MINUTE_TIME) / StlUtil.SECOND_TIME;
        if (secondTime > 0) {
            if (secondTime < 10) {
                timeBf.append("0" + secondTime);
            } else {
                timeBf.append("" + secondTime);
            }
        } else {
            timeBf.append("00");
        }
        return timeBf.toString();
    }

}
