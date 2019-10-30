package org.pkucare.spring;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.FileUrlResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 系统启动事件监听
 * Created by weiqin on 2019/7/6.
 */
public class MyApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MyApplicationListener.class);

    /**
     * 该事件可以用来做准备工作
     *
     * @param event
     */
    @Override
    @CachePut
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("开始读取 excel 数据文件");
        ApplicationContext context = event.getApplicationContext();
        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        Cache certificatesURL = cacheManager.getCache(Constant.EHCACHE_CERTIFICATE_URL);
        Cache certificatesInfo = cacheManager.getCache(Constant.EHCACHE_CERTIFICATES_INFO);
        String dataConfigPath = context.getEnvironment().getProperty(Constant.ASSETS_PATH);
        int countor = 0;
        // 加载数据到缓存里面
        FileInputStream file = null;
        try {
            file = new FileInputStream(new FileUrlResource(dataConfigPath).getFile());
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            DecimalFormat df = new DecimalFormat("0");
            CertificateInfo certificateInfo = null;
            for (Row row : sheet) {
                String phone = "";
                if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                    phone = df.format(row.getCell(2).getNumericCellValue()).trim();
                } else {
                    phone = row.getCell(2).getStringCellValue().trim();
                }
                if (null == phone) {
                    break;
                }
                certificateInfo = new CertificateInfo();
                certificateInfo.setUserName(row.getCell(0).getStringCellValue().trim());
                certificateInfo.setSerialNum(row.getCell(1).getStringCellValue().trim());
                certificateInfo.setCertificateName(row.getCell(4).getStringCellValue().trim());
                certificateInfo.setIssueTime(row.getCell(5).getStringCellValue().trim());
                certificateInfo.setIssuingUnit(row.getCell(6).getStringCellValue().trim());
                String fileName = row.getCell(3).getStringCellValue().trim();
                certificatesURL.put(phone, fileName);
                certificatesInfo.put(certificateInfo.getSerialNum(), certificateInfo);
                ++countor;
            }
        } catch (IOException e) {
            throw new RuntimeException("初始化文件数据时失败了！！！");
        }finally {
            if (file != null){
                try {
                    file.close();
                } catch (IOException e) {
                }
            }
        }
        logger.info("结束读取 excel 数据文件, 共加载了[{}]条记录", countor);
    }
}
