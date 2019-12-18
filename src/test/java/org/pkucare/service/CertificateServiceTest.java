package org.pkucare.service;

import org.junit.Test;
import org.pkucare.SpringBootApplicationTest;
import org.pkucare.pojo.CertificateInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by weiqin on 2019/12/17.
 */
public class CertificateServiceTest extends SpringBootApplicationTest {

    @Autowired
    private CertificateService certificateService;

    @Test
    public void testQueryByStr(){

        String queryStr = "ABA-Intro2019-002903";
        List<CertificateInfo> certificateInfoList = certificateService.queryCertBySerialNum(queryStr);
        System.out.println(certificateInfoList.toString());
    }

}
