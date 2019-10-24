package org.pkucare.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Random;

/**
 * Created by weiqin on 2019/6/14.
 */
@Service
@CacheConfig(cacheNames = {"verificationCodeCache"})
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    @Resource
    private CacheManager cacheManager;

    @Value("${sms.dayu.api}")
    private String smsApi;

    @Value("${sms.dayu.appkey}")
    private String appkey;

    @Value("${sms.dayu.secret}")
    private String secret;

    @Value("${certificate.file.config.path}")
    private String dataConfigPath;

    /**
     *
     * @param phone
     * @return
     * @throws ApiException
     */
    @CachePut(key = "#phone")
    public String sendVerification(String phone) throws ApiException {
        // 短信模板的内容 4位随机验证
        int code = new Random().nextInt(8999) + 1000;

        String json = "{\"code\":\"" + code + "\",\"product\":\"证书查询\"}";
        TaobaoClient client = new DefaultTaobaoClient(smsApi, appkey, secret);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType("normal");
        req.setSmsFreeSignName(Constant.DAYU_SIGN_PRODUCT);
        req.setSmsParamString(json);
        req.setRecNum(phone);
        req.setSmsTemplateCode("SMS_4395149");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        rsp = client.execute(req);
        logger.info("短信发送响应：" + rsp.getBody());
        Boolean success = null;
        try {
            JSONObject ob = JSON.parseObject(rsp.getBody().toString());
            JSONObject obj = ob.getJSONObject("alibaba_aliqin_fc_sms_num_send_response");
            JSONObject object = obj.getJSONObject("result");
            success = object.getBoolean("success");
            if (!success) {
                throw new ApiException("短信发送失败了");
            }
            logger.info("手机号[{}]短信发送成功了", phone);
        } catch (Exception e) {
            logger.info("手机号[{}]短信发送失败了", phone);
            throw new ApiException("短信发送失败了");
        }
        return String.valueOf(code);
    }

    /**
     * get value from cache
     * Cacheable 注解表示在每次执行前都会check 是否存在，
     * 如果存在直接在方法体返回，否则等方法执行完后把结果返回并缓存
     * @param phone
     * @return
     */
    @Cacheable(key = "#phone", unless = "(null != #result) && !(#result eq -1)")
    public String getVerification(String phone) {
        logger.warn("没有找到手机号[{}]对应的验证码！", phone);
        return "-1";
    }

    /**
     * 获取所有证书
     * 第一次从文件中取，后面会从缓存中取
     *
     * @return
     */
    public String getCertificateNameByPhone(String phone) throws IOException {
        Cache cache = cacheManager.getCache(Constant.EHCACHE_CERTIFICATE_URL);
        Cache.ValueWrapper fileName = cache.get(phone);
        String fileN = fileName == null ? null : (String) fileName.get();
        logger.info("手机号：{} 获取了证书名称 {} !", phone, fileN);
        return fileN;
    }

    /**
     * 根据 证书编号 查询证书信息
     * @param serialNum
     * @return
     */
    public CertificateInfo queryCertificateInfo(String serialNum) {
        Cache cache = cacheManager.getCache(Constant.EHCACHE_CERTIFICATES_INFO);
        Cache.ValueWrapper certificateInfo = cache.get(serialNum);
        logger.info("证书查询， 编号信息：{}", serialNum);
        if (certificateInfo != null) {
            return (CertificateInfo) certificateInfo.get();
        }else {
            return null;
        }
    }
}
