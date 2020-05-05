package org.pkucare.config.certificateTemplate;

import com.google.zxing.WriterException;
import org.pkucare.pojo.CertificateInfo;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

public abstract class CertificateTemplate {

    @Value("${certificate.baseurl}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void initTemplate(){

    }

    public abstract void generateCertificateImg(CertificateInfo certificateInfo) throws IOException;

    public final void generateCertificate(CertificateInfo certificateInfo){
        // 初始化模板引擎
        initTemplate();
        // 填充模板
        try {
            generateCertificateImg(certificateInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum PlaceHolderName{
        qrCode,
        certificateImg,
        issueTime,
        serialNum,
        userName,
        backgroundImg;
    }
}