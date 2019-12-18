package org.pkucare.pojo;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户证书相关的信息
 * Created by weiqin on 2019/9/23.
 */
@Document(collection = "certificate")
public class CertificateInfo {

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 证书编号
     */
    @Indexed
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

    /**
     * 身份证
     */
    @Indexed
    private String idCard;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 手机号
     */
    private String phone;

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
