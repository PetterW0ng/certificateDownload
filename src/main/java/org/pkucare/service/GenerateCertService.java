package org.pkucare.service;

import org.pkucare.config.certificateTemplate.CertificateTemplate;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.constant.Constant;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 证书生成服务
 * @author wq
 */
@Service
public class GenerateCertService {

    @Resource(name = "advancedTemplate")
    private CertificateTemplate advancedTemplate;

    public void generateCertificate(List<CertificateInfo> certificateInfoList){

        certificateInfoList.stream().forEach(certificateInfo -> {
            if (certificateInfo.getSerialNum().indexOf(Constant.CERTIFICATE_ADVANCED_PREFIX) > 0){
                advancedTemplate.generateCertificate(certificateInfo);
            }

        });

    }

}
