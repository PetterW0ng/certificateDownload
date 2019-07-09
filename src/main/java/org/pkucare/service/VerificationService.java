package org.pkucare.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by weiqin on 2019/6/14.
 */
@Service
@CacheConfig(cacheNames = {"verificationCodeCache"})
public class VerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationService.class);
    private static final String product = "产品名称";

    @Value("${sms.dayu.api}")
    private String smsApi;

    @Value("${sms.dayu.appkey}")
    private String appkey;

    @Value("${sms.dayu.secret}")
    private String secret;

    @Value("${certificate.file.config.path}")
    private String dataConfigPath;

    @Cacheable(key = "#phone")
    public String sendVerification(String phone) throws ApiException {
        // 短信模板的内容 4位随机验证
        int code = new Random().nextInt(8999) + 1000;

        String json = "{\"code\":\"" + code + "\",\"product\":\"证书查询\"}";
        TaobaoClient client = new DefaultTaobaoClient(smsApi, appkey, secret);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType("normal");
        req.setSmsFreeSignName(product);
        req.setSmsParamString(json);
        req.setRecNum(phone);
        req.setSmsTemplateCode("SMS_4395149");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        rsp = client.execute(req);
        LOGGER.info("短信发送响应：" + rsp.getBody());
        Boolean success = null;
        try {
            JSONObject ob = JSON.parseObject(rsp.getBody().toString());
            JSONObject obj = ob.getJSONObject("alibaba_aliqin_fc_sms_num_send_response");
            JSONObject object = obj.getJSONObject("result");
            success = object.getBoolean("success");
            if (!success) {
                throw new ApiException("短信发送失败了");
            }
            LOGGER.info("手机号[{}]短信发送成功了", phone);
        } catch (Exception e) {
            LOGGER.info("手机号[{}]短信发送失败了", phone);
            throw new ApiException("短信发送失败了");
        }
        return String.valueOf(code);
    }

    @Cacheable(key = "#phone", unless = "(null != #result) && !(#result eq -1)")
    public String getVerification(String phone) {
        LOGGER.warn("没有找到手机号[{}]对应的验证码！", phone);
        return "-1";
    }

    /**
     * 获取所有证书
     * 第一次从文件中取，后面会从缓存中取
     *
     * @return
     */
    @Cacheable(value = "certificatesMap", key = "#root.methodName")
    public Map<String, String> getCertificates() throws IOException {
        Map<String, String> certificatesMap = new HashMap<>();
        // 加载数据到内存里面
        LOGGER.info("加载所有证书至缓存中-开始...");
        FileInputStream file = new FileInputStream(new FileUrlResource(dataConfigPath).getFile());

        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        DecimalFormat df = new DecimalFormat("0");
        for (Row row : sheet) {
            String phone = "", fileName = "";
            if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                phone = df.format(row.getCell(2).getNumericCellValue()).trim();
            } else {
                phone = row.getCell(2).getStringCellValue().trim();
            }
            if (row.getCell(4) != null) {
                fileName = row.getCell(4).getStringCellValue();
            }
            if (null == phone && null == fileName) {
                break;
            }
            certificatesMap.put(phone, fileName);
        }
        LOGGER.info("加载所有证书至缓存中-结束, 共加载了[{}]条记录", certificatesMap.size());
        return certificatesMap;
    }


}
