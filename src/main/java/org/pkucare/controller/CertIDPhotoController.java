package org.pkucare.controller;

import com.alibaba.fastjson.JSONObject;
import org.pkucare.pojo.CertIDPhoto;
import org.pkucare.pojo.Response;
import org.pkucare.pojo.constant.Constant;
import org.pkucare.pojo.constant.ResultCode;
import org.pkucare.service.CertIDPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = {"/certIDPhoto"})
public class CertIDPhotoController {

    private static final Logger logger = LoggerFactory.getLogger(CertIDPhotoController.class);
    @Autowired
    private CertIDPhotoService certIDPhotoService;

    @Value("${user.certificate.img.path}")
    private String userImgPath;

    /**
     * 导入 需要上传的人员列表
     */
    @RequestMapping(value = "/uploadLimitList", method = RequestMethod.POST)
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file) {
        logger.info("开始上传文件 fileName ={}", file.getOriginalFilename());
        Integer importCount = certIDPhotoService.importExcel2Mongo(file);
        logger.info("结束上传文件 fileName ={}，共新增数据 {} 条", file.getName(), importCount);
        ModelAndView mv = new ModelAndView("success");
        mv.getModel().put("message", "共导入数据 " + importCount + " 条");
        return mv;
    }

    /**
     * 根据 批次号
     * 导出 需要上传的人员列表
     */
    @RequestMapping(value = "/exportLimitList", method = RequestMethod.POST)
    public void handleExportLimitList(@RequestParam String batchNum, HttpServletResponse response) {
        logger.info("开始export , batchNum ={}", batchNum);
        certIDPhotoService.exportData2Excel(response, batchNum);
        logger.info("结束export , batchNum ={}", batchNum);
    }

    @RequestMapping(value = "/uploadHeadImg", method = RequestMethod.POST)
    public Response uploadHeadImg(@RequestParam("file") MultipartFile file, @RequestParam String idCard, @RequestParam String certificateType) throws IOException {
        Response response = Response.SUCCESS();
        // 先判断 证件照是否合规
        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        boolean matchType = ".png".equals(suffix) || ".jpg".equals(suffix);
        if (!matchType) {
            response.setResultCode(ResultCode.FILE_TYPE_UNSUITABLE);
        } else if (1024 * 1024 < file.getSize()) {
            response.setResultCode(ResultCode.FILE_SIZE_UNSUITABLE);
        } else {
            // 校验是否允许上传
            CertIDPhoto certIDPhoto = certIDPhotoService.queryLatestByIdCard(idCard);
            if (null == certIDPhoto) {
                response.setResultCode(ResultCode.NO_IDCARD);
            }else if (certIDPhoto.getBeginTime().getTime() > System.currentTimeMillis() || certIDPhoto.getEndTime().getTime() < System.currentTimeMillis()) {
                response.setResultCode(ResultCode.NOT_WITHIN_UPLOAD_TIME);
            } else {
                // 保存图片
                String fileName =  idCard + Constant.DOT + Constant.CERTIFICATE_HEAD_IMG;
                File desFile = new File(userImgPath + fileName);
                BufferedImage input = ImageIO.read(file.getInputStream());
                ImageIO.write(input, Constant.CERTIFICATE_HEAD_IMG, desFile);
                // 添加人员的 绑定关系
                certIDPhoto.setUploaded(Boolean.TRUE);
                certIDPhotoService.modify(certIDPhoto);
                logger.info("身份证 idCard = {}, 上传了 证件照片 = {}", idCard, fileName);
                response.setData("证件照上传成功！");
            }
        }
        return response;
    }
}
