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
     * 证书编号
     */
    private String serialNum;

    /**
     * 证书名称
     */
    private String certificateName;

    /**
     * 发证时间
     */
    private String issueTime;

    /**
     * 发证单位
     */
    private String issuingUnit;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getIssuingUnit() {
        return issuingUnit;
    }

    public void setIssuingUnit(String issuingUnit) {
        this.issuingUnit = issuingUnit;
    }
}
