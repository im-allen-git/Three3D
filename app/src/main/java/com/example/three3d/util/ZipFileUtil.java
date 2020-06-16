package com.example.three3d.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipFileUtil {

    /**
     * 压缩文件
     * srcFileString 要压缩的文件或文件夹
     * zipFileString 压缩完成的Zip路径
     * 将文件进行压缩
     */
    public static void ZipFolder(String srcFileString, String zipFileString) {

        ZipOutputStream outZip = null;
        FileOutputStream fileOutputStream = null;
        try {
            //创建Zip包
            fileOutputStream = new FileOutputStream(zipFileString);
            outZip = new ZipOutputStream(fileOutputStream);

            //打开要输出的文件
            File file = new File(srcFileString);

            //压缩
            ZipFiles(file.getParent() + java.io.File.separator, file.getName(), outZip);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //完成,关闭
            try {
                outZip.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outZip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 压缩功能
     */
    private static void ZipFiles(String folderString, String fileString, java.util.zip.ZipOutputStream zipOutputSteam) throws Exception {

        if (zipOutputSteam == null)
            return;

        java.io.File file = new java.io.File(folderString + fileString);

        //判断是不是文件
        if (file.isFile()) {

            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(fileString);
            java.io.FileInputStream inputStream = new java.io.FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];

            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }

            zipOutputSteam.closeEntry();
        } else {

            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(fileString + java.io.File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (String aFileList : fileList) {
                ZipFiles(folderString, fileString + File.separator + aFileList, zipOutputSteam);
            }

        }
    }

    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     *
     * @throws Exception
     */
    public static int upZipFile(File zipFile, String folderPath) {
        //public static void upZipFile() throws Exception{
        ZipFile zfile = null;
        try {
            zfile = new ZipFile(zipFile);
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                if (ze.isDirectory()) {
                    String dirstr = folderPath + "/" + ze.getName();
                    //dirstr.trim();
                    try {
                        dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("3 e " + e.toString());
                        e.printStackTrace();
                    }
                    File f = new File(dirstr);
                    f.mkdir();
//                System.out.println("f = " + f.getName());
                    continue;
                }
                File flocal = getRealFileName(folderPath, ze.getName());
//            System.out.println("ze.getName() = " + ze.getName());
                if (flocal == null) {
                    break;
                }
                OutputStream os = null;
                try {
//              System.out.println("flocal = " + flocal.exists() + " " + flocal.getAbsolutePath());
                    os = new BufferedOutputStream(new FileOutputStream(flocal));
                } catch (FileNotFoundException e) {
                    System.out.println("4 e " + e.toString());
                    e.printStackTrace();
                }
                InputStream is = null;
                try {
                    is = new BufferedInputStream(zfile.getInputStream(ze));
                } catch (IOException e) {
                    System.out.println("5 e " + e.toString());
                    e.printStackTrace();
                }
                int readLen = 0;
                try {
                    while ((readLen = is.read(buf, 0, 1024)) != -1) {
                        os.write(buf, 0, readLen);
                    }
                    os.flush();
                    is.close();
                    os.close();
                } catch (IOException e) {
                    System.out.println("6 e " + e.toString());
                    e.printStackTrace();
                }
            }
            try {
                zfile.close();
            } catch (IOException e) {
                System.out.println("7 e " + e.toString());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        for (String sss : dirs) {
//          System.out.println("sss = " + sss);
        }
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
//              System.out.println("substr = " + substr);
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    System.out.println("8 e " + e.toString());
                    return null;
                }
                ret = new File(ret, substr);

            }
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
            } catch (UnsupportedEncodingException e) {
                System.out.println("9 e " + e.toString());
                e.printStackTrace();
                return null;
            }

            ret = new File(ret, substr);
//            System.out.println("getRealFileName ret = " + ret.getAbsolutePath());
            return ret;
        } else {
            ret = new File(baseDir + "/" + absFileName);
        }
//        System.out.println("getRealFileName ret = " + ret.getAbsolutePath());
        return ret;
    }
}
