package org.pkucare.pojo;

import org.pkucare.annotation.ExcelExport;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "certIDPhoto")
public class CertIDPhoto {

    /**
     * 证书编号
     */
    @Id
    @ExcelExport("证书编号")
    private String serialNum;

    /**
     * 姓名
     */
    @ExcelExport("姓名")
    private String userName;


    /**
     * 证书名称
     */
    @ExcelExport("证书名称")
    private String certificateName;

    /**
     * 身份证号
     */
    @Indexed
    @ExcelExport("身份证号")
    private String idCard;

    /**
     * 手机号
     */
    @ExcelExport("手机号")
    private String phone;

    /**
     * 允许上传的开始时间
     */
    @ExcelExport(value = "开始上传时间", dataFormate ="yyyy/MM/dd")
    private Date beginTime;

    /**
     * 允许上传的结束时间
     */
    @ExcelExport(value = "结束上传时间", dataFormate ="yyyy/MM/dd")
    private Date endTime;

    /**
     * 是否上传了照片
     * true: 已上传
     * false: 未上传
     */
    @ExcelExport("是否上传")
    private Boolean uploaded;

    /**
     * 批次号
     */
    @ExcelExport("批次号")
    private Double batchNum;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @ExcelExport(value = "最近更新时间", dataFormate ="yyyy/MM/dd")
    private Date updateTime;

    /**
     * 创建时间
     */
    @CreatedDate
    private Date createTime;

    /**
     * 证书名称
     */
    @ExcelExport(value = "文件名称")
    private String fileName;

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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Double batchNum) {
        this.batchNum = batchNum;
    }
}
