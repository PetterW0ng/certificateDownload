package org.pkucare.service;

import org.junit.Before;
import org.junit.Test;
import org.pkucare.SpringBootApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CertIDPhotoServiceTest extends SpringBootApplicationTest {

    @Autowired
    private CertIDPhotoService certIDPhotoService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Test
    public void testExportData2Excel(){
        String batchNum = "1.0";
        certIDPhotoService.exportData2Excel(response, batchNum);
    }

    @Before
    public void setUp(){
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        request.addHeader("USER-AGENT", "");
        response = new MockHttpServletResponse();
    }

}
