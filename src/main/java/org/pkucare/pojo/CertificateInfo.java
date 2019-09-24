package org.pkucare.pojo;

/**
 * 用户证书相关的信息
 * Created by weiqin on 2019/9/23.
 */
public class CertificateInfo {

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String phoneNum;

    /**
     * 证书编号
     */
    private String serialNum;

    /**
     * 证书名称 下载使用
     */
    private String fileName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
