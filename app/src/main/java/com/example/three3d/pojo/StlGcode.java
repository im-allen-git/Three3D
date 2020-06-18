package com.example.three3d.pojo;

public class StlGcode {

    private int id;

    // stl原始文件名称
    private String sourceStlName;

    // 真实文件名称
    private String realStlName;

    // stl压缩文件名称
    private String sourceZipStlName;

    // 服务器返回文件
    private String serverZipGcodeName;

    // 本地解压文件
    private String localGcodeName;

    private String createTime;


    public StlGcode() {
    }

    public StlGcode(int id, String sourceStlName, String realStlName, String sourceZipStlName, String serverZipGcodeName, String localGcodeName, String createTime) {
        this.id = id;
        this.sourceStlName = sourceStlName;
        this.realStlName = realStlName;
        this.sourceZipStlName = sourceZipStlName;
        this.serverZipGcodeName = serverZipGcodeName;
        this.localGcodeName = localGcodeName;
        this.createTime = createTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourceStlName() {
        return sourceStlName;
    }

    public void setSourceStlName(String sourceStlName) {
        this.sourceStlName = sourceStlName;
    }

    public String getRealStlName() {
        return realStlName;
    }

    public void setRealStlName(String realStlName) {
        this.realStlName = realStlName;
    }

    public String getSourceZipStlName() {
        return sourceZipStlName;
    }

    public void setSourceZipStlName(String sourceZipStlName) {
        this.sourceZipStlName = sourceZipStlName;
    }

    public String getServerZipGcodeName() {
        return serverZipGcodeName;
    }

    public void setServerZipGcodeName(String serverZipGcodeName) {
        this.serverZipGcodeName = serverZipGcodeName;
    }

    public String getLocalGcodeName() {
        return localGcodeName;
    }

    public void setLocalGcodeName(String localGcodeName) {
        this.localGcodeName = localGcodeName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "StlGcode{" +
                "id=" + id +
                ", sourceStlName='" + sourceStlName + '\'' +
                ", realStlName='" + realStlName + '\'' +
                ", sourceZipStlName='" + sourceZipStlName + '\'' +
                ", serverZipGcodeName='" + serverZipGcodeName + '\'' +
                ", localGcodeName='" + localGcodeName + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
