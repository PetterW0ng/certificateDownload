package org.pkucare.controller;

import com.taobao.api.ApiException;
import org.pkucare.annotation.RequestLimit;
import org.pkucare.exception.RequestLimitException;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.Response;
import org.pkucare.pojo.constant.Constant;
import org.pkucare.service.VerificationService;
import org.pkucare.util.PatternUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * 用户下载证书
 * Created by weiqin on 2019/6/14.
 */
@RestController
@RequestMapping(value = "/certificate")
@PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound = true)
public class CertificateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateController.class);
    private static final String INVALID_PHONE = "手机号不正确";

    @Autowired
    private VerificationService verificationService;

    @Value("${certificate.file.data.path}")
    private String dataPath;

    @RequestLimit(count = 3)
    @RequestMapping(value = "/getDownloadURL", method = RequestMethod.POST)
    public Response doVerification(@RequestParam String phone, @RequestParam String verificationCode, HttpServletRequest request) throws RequestLimitException, IOException {
        Response<String> response = new Response<>();
        // 1、验证手机号码及验证码
        String code = verificationService.getVerification(phone);
        if (verificationCode.equals(code)) {
            // 2、下发下载证书的地址, 从缓存中获取
            String res = verificationService.getCertificateNameByPhone(phone);
            LOGGER.warn("手机号[{}]输入的验证码[{}]--正确！", phone, verificationCode);
            response.setData(res);
        } else {
            LOGGER.warn("手机号[{}]输入的验证码[{}]--错误！", phone, verificationCode);
            response.setMessage("验证码不正确！");
            response.setCode(-1);
        }
        return response;
    }

    @RequestMapping(value = "/download/{file}")
    public ResponseEntity<byte[]> certificateDownload(@PathVariable String file) throws FileNotFoundException {
        String fileName = file + Constant.FILE_TYPE;
        //将该文件加入到输入流之中
        ResponseEntity<byte[]> response = null;
        InputStream in = null;
        try {
            in = new FileInputStream(new FileUrlResource(dataPath + fileName).getFile());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("Content-Disposition", "attachment;filename=" + fileName);
            HttpStatus statusCode = HttpStatus.OK;
            response = new ResponseEntity<>(FileCopyUtils.copyToByteArray(in), headers, statusCode);
            LOGGER.warn("证书[{}]下载成功！", fileName);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }

        return response;
    }

    /**
     * 获取验证码信息
     *
     * @param phone
     * @return
     * @throws ApiException
     * @throws IOException
     */
    @RequestLimit(count = 2)
    @RequestMapping(value = "/getVerificationCode", method = RequestMethod.POST)
    public Response getVerificationCode(@RequestParam String phone, HttpServletRequest request) throws ApiException, RequestLimitException, IOException {
        Response response = new Response<String>();
        // 1、验证手机号格式
        if (PatternUtil.testPhone(phone)) {
            // 2、验证该手机号是否考过证书
            if (StringUtils.isEmpty(verificationService.getCertificateNameByPhone(phone))) {
                response.setMessage("请确认该手机号是否为考试时所留或证书还没有生成！");
                response.setCode(-2);
                return response;
            }
            // 3、发送验证到手机 该验证码5分钟内有效
            String code = verificationService.sendVerification(phone);
            LOGGER.info("发送到 [{}] 的验证码为 [{}]", phone, code);
        } else {
            response.setCode(-1);
            response.setData(INVALID_PHONE);
        }
        return response;
    }

    /**
     * 查询证书信息
     *
     * @param serialNum
     * @return
     * @throws ApiException
     * @throws IOException
     */
    @RequestMapping(value = "/queryCertificateInfo", method = RequestMethod.POST)
    public Response queryCertificateInfo(@RequestParam String serialNum) {
        Response response = new Response<String>();
        if (StringUtils.isEmpty(serialNum)) {
            response.setCode(-1);
            response.setMessage("证书编号为空,重新填写！");
        } else {
            CertificateInfo certificateInfo = verificationService.queryCertificateInfo(serialNum);
            response.setData(certificateInfo);
        }
        return response;
    }


}
