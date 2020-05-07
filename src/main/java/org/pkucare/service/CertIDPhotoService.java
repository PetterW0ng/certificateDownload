package org.pkucare.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pkucare.exception.ValidateException;
import org.pkucare.pojo.CertIDPhoto;
import org.pkucare.repository.CertIDPhotoRepository;
import org.pkucare.util.ExcelUtil;
import org.pkucare.util.MessageDigestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class CertIDPhotoService {

    public static final Logger logger = LoggerFactory.getLogger(CertIDPhotoService.class);

    @Autowired
    private CertIDPhotoRepository certIDPhotoRepository;


    public Integer importExcel2Mongo(MultipartFile mFile) throws ValidateException {
        // 加载数据到缓存里面
        InputStream file = null;
        try {
            file = mFile.getInputStream();
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            DecimalFormat df = new DecimalFormat("0");
            CertIDPhoto certIDPhoto = null;
            List<CertIDPhoto> certificateInfoList = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row.getCell(2) == null || "".equals(row.getCell(2).getStringCellValue())) {
                    break;
                }
                certIDPhoto = new CertIDPhoto();
                certIDPhoto.setUserName(row.getCell(1).getStringCellValue().trim());
                certIDPhoto.setSerialNum(row.getCell(2).getStringCellValue().trim());
                certIDPhoto.setPhone(row.getCell(3).getStringCellValue().trim());
                certIDPhoto.setIdCard(row.getCell(4).getStringCellValue().trim());
                certIDPhoto.setCertificateName(row.getCell(5).getStringCellValue().trim());
                certIDPhoto.setBeginTime(row.getCell(6).getDateCellValue());
                certIDPhoto.setEndTime(row.getCell(7).getDateCellValue());
                certIDPhoto.setBatchNum(row.getCell(8).getNumericCellValue());
                certIDPhoto.setUploaded(Boolean.FALSE);
                certIDPhoto.setFileName(MessageDigestUtil.getCertificateName(certIDPhoto.getSerialNum()));
                certificateInfoList.add(certIDPhoto);
            }
            certIDPhotoRepository.saveAll(certificateInfoList);
            logger.info("数据文件加载成功，共加载了 {} 条数据", certificateInfoList.size());
            return certificateInfoList.size();
        } catch (Exception e) {
            throw new ValidateException("excel 表格导入失败了，请参照模板格式！ 原因：" + e.getMessage(), e);
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public CertIDPhoto queryLatestByIdCard(String idCard) {
        return certIDPhotoRepository.findFirstByIdCardOrderByUpdateTimeDesc(idCard);
    }

    public void modify(CertIDPhoto certIDPhoto) {
        certIDPhotoRepository.save(certIDPhoto);
    }

    public void exportData2Excel(HttpServletResponse response, String batchNum) {
        List<CertIDPhoto> certIDPhotoList = certIDPhotoRepository.queryAllByBatchNum(Double.valueOf(batchNum));
        String fileName = "证件照上传情况第" + Integer.valueOf(batchNum) + "批次", sheetName = "证件照上传情况";
        ExcelUtil.export(fileName, sheetName, certIDPhotoList, response);
    }
}
