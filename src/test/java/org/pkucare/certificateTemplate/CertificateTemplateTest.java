package org.pkucare.certificateTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.pkucare.SpringBootApplicationTest;
import org.pkucare.config.certificateTemplate.CertificateTemplate;
import org.pkucare.pojo.CertificateInfo;

import javax.annotation.Resource;

public class CertificateTemplateTest extends SpringBootApplicationTest {

    @Resource(name = "advancedTemplate")
    private CertificateTemplate advancedTemplate;


    @Test
    public void testGenerateCertificate(){
        String certJSON = "{ \n" +
                "    \"userName\" : \"扩木那\", \n" +
                "    \"serialNum\" : \"BISA-210404199602290023\", \n" +
                "    \"certificateName\" : \"ABA孤独症康复专业技能培训证书\", \n" +
                "    \"issueTime\" : \"2020.10.25\", \n" +
                "    \"idCard\" : \"350524197805190041\", \n" +
                "    \"issuingUnit\" : \"中国残疾人联合会社会服务指导中心、北医脑健康行为发展教研院\", \n" +
                "    \"fileName\" : \"CF6A1F8248B765F5576D0A7A5A630C34\", \n" +
                "    \"phone\" : \"13476140598\"\n" +
                "}";
        CertificateInfo certificateInfo = JSONObject.toJavaObject(JSON.parseObject(certJSON), CertificateInfo.class);
        advancedTemplate.generateCertificate(certificateInfo);
    }
}
