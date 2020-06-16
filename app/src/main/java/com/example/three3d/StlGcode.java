package com.example.three3d;

import java.time.LocalDateTime;

public class StlGcode {

    // stl原始文件名称
    private String sourceStlFile;

    // 真实文件名称
    private String realStlFile;

    // stl压缩文件名称
    private String srStlZipFile;

    // 服务器返回文件
    private String serverGcodeZipFile;

    // 本地解压文件
    private String localGcodeFile;

    private String createTime;

    public String getSourceStlFile() {
        return sourceStlFile;
    }

    public void setSourceStlFile(String sourceStlFile) {
        this.sourceStlFile = sourceStlFile;
    }

    public String getSrStlZipFile() {
        return srStlZipFile;
    }

    public String getRealStlFile() {
        return realStlFile;
    }

    public void setRealStlFile(String realStlFile) {
        this.realStlFile = realStlFile;
    }

    public void setSrStlZipFile(String srStlZipFile) {
        this.srStlZipFile = srStlZipFile;
    }

    public String getServerGcodeZipFile() {
        return serverGcodeZipFile;
    }

    public void setServerGcodeZipFile(String serverGcodeZipFile) {
        this.serverGcodeZipFile = serverGcodeZipFile;
    }

    public String getLocalGcodeFile() {
        return localGcodeFile;
    }

    public void setLocalGcodeFile(String localGcodeFile) {
        this.localGcodeFile = localGcodeFile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
