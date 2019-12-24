package org.pkucare.controller;

import com.taobao.api.ApiException;
import org.pkucare.annotation.RequestLimit;
import org.pkucare.exception.RequestLimitException;
import org.pkucare.exception.ValidateException;
import org.pkucare.pojo.Response;
import org.pkucare.pojo.constant.Constant;
import org.pkucare.service.CertificateService;
import org.pkucare.service.VerificationService;
import org.pkucare.util.MessageDigestUtil;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
@RequestMapping(value = {"/cert"})
@PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound = true)
public class CertificateController {

    private static final Logger logger = LoggerFactory.getLogger(CertificateController.class);
    private static final String INVALID_PHONE = "手机号不正确";

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private CertificateService certificateService;

    @Value("${certificate.file.data.path}")
    private String dataPath;

    /**
     * 获取下载地址
     *
     * @param phone
     * @param verificationCode
     * @return
     * @throws RequestLimitException
     * @throws IOException
     */
    @RequestLimit(count = 3)
    @RequestMapping(value = "/getDownloadURL", method = RequestMethod.POST)
    public Response doVerification(@RequestParam String phone, @RequestParam String verificationCode, HttpServletRequest request) throws RequestLimitException, IOException {
        Response<String> response = new Response<>();
        // 1、验证手机号码及验证码
        String code = verificationService.getVerification(phone);
        if (verificationCode.equals(code)) {
            // 2、下发下载证书的地址, 从缓存中获取
            String url = "/cert/queryCertByPhone?phone=" + phone +"&sign=" + MessageDigestUtil.md5(phone);
            logger.warn("手机号[{}]输入的验证码[{}]--正确！", phone, verificationCode);
            response.setData(url);
        } else {
            logger.warn("手机号[{}]输入的验证码[{}]--错误！", phone, verificationCode);
            response.setMessage("验证码不正确！");
            response.setCode(-1);
        }
        return response;
    }

    /**
     * 下载证书
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
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
            logger.warn("证书[{}]下载成功！", fileName);
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
     * @param request 该参数在对用户请求次数上使用了
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
            if (CollectionUtils.isEmpty(certificateService.queryCertByPhone(phone))) {
                response.setMessage("请确认该手机号是否为考试时所留或证书还没有生成！");
                response.setCode(-2);
                return response;
            }
            // 3、发送验证到手机 该验证码5分钟内有效
            String code = verificationService.sendVerification(phone);
            logger.info("发送到 [{}] 的验证码为 [{}]", phone, code);
        } else {
            response.setCode(-1);
            response.setData(INVALID_PHONE);
        }
        return response;
    }

    /**
     * 根据 证书编号/身份证号 查询证书信息
     *
     * @param queryStr
     * @return
     * @throws ApiException
     * @throws IOException
     */
    @RequestMapping(value = "/queryCertificateInfo", method = RequestMethod.POST)
    public ModelAndView queryCertificateInfo(@RequestParam(value = "serialNum") String queryStr) throws ValidateException {
        if (StringUtils.isEmpty(queryStr)) {
            throw new ValidateException();
        }
        ModelAndView mv = new ModelAndView("certificate");
        mv.getModel().put("certificateInfoList", certificateService.queryCertByQueryStr(queryStr));
        return mv;
    }

    /**
     * 根据 手机号 查询证书信息
     *
     * @param phone
     * @return
     * @throws ApiException
     * @throws IOException
     */
    @RequestMapping(value = "/queryCertByPhone", method = RequestMethod.GET)
    public ModelAndView queryCertByPhone(@RequestParam(required = false) String phone,@RequestParam(required = false) String sign) throws ValidateException {
        if (!PatternUtil.testPhone(phone) || StringUtils.isEmpty(sign)) {
            throw new ValidateException();
        }else if(!sign.equals(MessageDigestUtil.md5(phone))){
            throw new ValidateException("非法操作，签名错误！");
        }
        ModelAndView mv = new ModelAndView("certificate");
        mv.getModel().put("certificateInfoList", certificateService.queryCertByPhone(phone));
        return mv;
    }

    /**
     * 扫码 查询证书信息
     *
     * @param serialNum
     * @return
     * @throws ApiException
     * @throws IOException
     */
    @RequestMapping(value = "/{serialNum}", method = RequestMethod.GET)
    public ModelAndView queryCertificateBySerialNum(@PathVariable String serialNum) throws ValidateException {
        if (StringUtils.isEmpty(serialNum)) {
            throw new ValidateException();
        }
        ModelAndView mv = new ModelAndView("certificate");
        mv.getModel().put("certificateInfoList", certificateService.queryCertBySerialNum(serialNum));
        return mv;
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file) {
        logger.info("开始上传文件 fileName ={}", file.getOriginalFilename());
        Integer importCount = certificateService.excel2Mongo(file);
        logger.info("结束上传文件 fileName ={}，共新增数据 {} 条", file.getName(), importCount);
        ModelAndView mv = new ModelAndView("success");
        mv.getModel().put("message", "共导入数据 " + (importCount-1) + " 条");
        return mv;
    }

}
