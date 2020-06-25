package org.pkucare.service;

import org.pkucare.config.certificateTemplate.CertificateTemplate;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 证书生成服务
 * @author wq
 */
@Service
public class GenerateCertService {

    public static final Logger logger = LoggerFactory.getLogger(GenerateCertService.class);
    @Resource(name = "advancedTemplate")
    private CertificateTemplate advancedTemplate;

    @Resource(name = "abaTemplate")
    private CertificateTemplate abaTemplate;

    public void generateCertificate(List<CertificateInfo> certificateInfoList){

        certificateInfoList.stream().forEach(certificateInfo -> {
            if (certificateInfo.getSerialNum().toUpperCase().contains(Constant.CERTIFICATE_ADVANCED_PREFIX)){
                logger.info("{} 需要生成证书", certificateInfo.getIdCard());
                advancedTemplate.generateCertificate(certificateInfo);
            }else if (certificateInfo.getSerialNum().toUpperCase().contains(Constant.CERTIFICATE_ABA_PREFIX)){
                logger.info("{} 需要生成证书", certificateInfo.getSerialNum());
                abaTemplate.generateCertificate(certificateInfo);
            }

        });

    }

}
