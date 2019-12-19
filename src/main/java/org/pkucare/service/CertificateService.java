package org.pkucare.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.repository.CertificateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 证书服务相关
 * Created by weiqin on 2019/12/17.
 */
@Service
public class CertificateService {

    private static final Logger logger = LoggerFactory.getLogger(CertificateService.class);

    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * 根据 idCard 查询用户
     *
     * @param idCard
     * @return
     */
    public List<CertificateInfo> queryCertByIdCard(String idCard) {
        logger.info("根据身份证查询证书列表 idCard={}", idCard);
        return certificateRepository.queryCertByIdCard(idCard);
    }

    /**
     * 根据 serialNum 查询用户
     *
     * @param serialNum
     * @return
     */
    public List<CertificateInfo> queryCertBySerialNum(String serialNum) {
        logger.info("根据证书编号查询证书列表 serialNum={}", serialNum);
        return certificateRepository.queryCertBySerialNum(serialNum);
    }

    /**
     * 根据 手机号 查询用户
     *
     * @param phone
     * @return
     */
    public List<CertificateInfo> queryCertByPhone(String phone) {
        logger.info("根据手机号查询证书列表 phone={}", phone);
        return certificateRepository.queryCertByPhone(phone);
    }

    /**
     * 根据 idCard或serialNum 查询用户
     *
     * @param queryStr
     * @return
     */
    public List<CertificateInfo> queryCertByQueryStr(String queryStr) {
        List<CertificateInfo> certificateInfoSerialList = this.queryCertBySerialNum(queryStr);
        List<CertificateInfo> certificateInfoIdCardList = this.queryCertByIdCard(queryStr);
        certificateInfoIdCardList.addAll(certificateInfoSerialList);
        return certificateInfoIdCardList;
    }

    /**
     * 读取 excel file 后，保存数据至 mongo
     *
     * @return
     */
    public Integer excel2Mongo(MultipartFile mFile) {
        // 加载数据到缓存里面
        InputStream file = null;
        try {
            file = mFile.getInputStream();
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            DecimalFormat df = new DecimalFormat("0");
            CertificateInfo certificateInfo = null;
            List<CertificateInfo> certificateInfoList = new ArrayList<>();
            for (Row row : sheet) {
                String phone = "";
                if (row.getCell(3).getCellType() == CellType.NUMERIC) {
                    phone = df.format(row.getCell(3).getNumericCellValue()).trim();
                } else {
                    phone = row.getCell(3).getStringCellValue().trim();
                }
                if (null == phone) {
                    break;
                }
                certificateInfo = new CertificateInfo();
                certificateInfo.setUserName(row.getCell(1).getStringCellValue().trim());
                certificateInfo.setSerialNum(row.getCell(2).getStringCellValue().trim());
                certificateInfo.setCertificateName(row.getCell(5).getStringCellValue().trim());
                certificateInfo.setIssueTime(row.getCell(6).getStringCellValue().trim());
                certificateInfo.setIssuingUnit(row.getCell(7).getStringCellValue().trim());
                certificateInfo.setFileName(row.getCell(4).getStringCellValue().trim());
                certificateInfo.setPhone(phone);
                Cell idCardCell = row.getCell(8);
                if(null != idCardCell){
                    certificateInfo.setIdCard(idCardCell.getStringCellValue().trim());
                }
                certificateInfoList.add(certificateInfo);
            }
            certificateRepository.insert(certificateInfoList);
            logger.info("数据文件加载成功，共加载了 {} 条数据", certificateInfoList.size());
            return certificateInfoList.size();
        } catch (IOException e) {
            throw new RuntimeException("初始化文件数据时失败了！！！");
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                }
            }
        }

    }

}
