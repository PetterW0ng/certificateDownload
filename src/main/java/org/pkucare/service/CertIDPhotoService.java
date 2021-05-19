package org.pkucare.service;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CertIDPhotoService {

    public static final Logger logger = LoggerFactory.getLogger(CertIDPhotoService.class);

    @Autowired
    private CertIDPhotoRepository certIDPhotoRepository;


    public Integer importExcel2Mongo(MultipartFile mFile) throws ValidateException {
        // 加载数据到缓存里面
        InputStream file = null;
        int i = 0, j = 0;
        try {
            file = mFile.getInputStream();
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            DecimalFormat df = new DecimalFormat("0");
            CertIDPhoto certIDPhoto = null;
            List<CertIDPhoto> certificateInfoList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            for (i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                certIDPhoto = new CertIDPhoto();
                for (j = 1; j <= 8; j++) {
                    CellType cellType = row.getCell(j).getCellType();
                    String v = "";
                    if (cellType == CellType.NUMERIC) {
                        v = df.format(row.getCell(j).getNumericCellValue());
                    } else if (cellType == CellType.FORMULA) {
                        v = row.getCell(j).getCellFormula();
                    } else {
                        v = row.getCell(j).getStringCellValue().trim();
                    }
                    switch (j) {
                        case 1:
                            certIDPhoto.setUserName(v);
                            break;
                        case 2:
                            certIDPhoto.setSerialNum(v);
                            break;
                        case 3:
                            certIDPhoto.setPhone(v);
                            break;
                        case 4:
                            certIDPhoto.setIdCard(v);
                            break;
                        case 5:
                            certIDPhoto.setCertificateName(v);
                            break;
                        case 6:
                            certIDPhoto.setBeginTime(sdf.parse(v));
                            break;
                        case 7:
                            certIDPhoto.setEndTime(sdf.parse(v));
                            break;
                        case 8:
                            certIDPhoto.setBatchNum(Double.valueOf(v));
                            break;
                    }
                }
                certIDPhoto.setUploaded(Boolean.FALSE);
                certIDPhoto.setFileName(MessageDigestUtil.getCertificateName(certIDPhoto.getSerialNum()));
                certificateInfoList.add(certIDPhoto);
            }
            certIDPhotoRepository.saveAll(certificateInfoList);
            logger.info("数据文件加载成功，共加载了 {} 条数据", certificateInfoList.size());
            return certificateInfoList.size();
        } catch (Exception e) {
            throw new ValidateException("excel 表格导入失败了，请参照模板格式！ 第" + i + "行,第" + j + "列出错了， 原因：请确认该列是否为文本，如果为日期请按照yyyy.MM.dd 格式", e);
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
