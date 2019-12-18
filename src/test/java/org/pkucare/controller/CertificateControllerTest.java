package org.pkucare.controller;

import org.junit.Test;
import org.pkucare.SpringBootApplicationTest;
import org.pkucare.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by weiqin on 2019/12/17.
 */
public class CertificateControllerTest extends SpringBootApplicationTest {

    @MockBean
    private CertificateService certificateService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void testFileUpload() throws Exception {

        MockMultipartFile multipartFile = new MockMultipartFile("file", "C:\\data\\renyuanmingdan.xlsx",
                "xls/plain", "Spring Framework".getBytes());
        String content = this.mvc.perform(fileUpload("/cert/uploadFile").file(multipartFile))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        System.out.println(content);
        then(this.certificateService).should().excel2Mongo(multipartFile);
    }


}
