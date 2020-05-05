package org.pkucare.config.certificateTemplate;

import com.google.zxing.WriterException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.impl.CTRegularTextRunImpl;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.constant.Constant;
import org.pkucare.util.MessageDigestUtil;
import org.pkucare.util.QrCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@Component
public class AdvancedTemplate extends CertificateTemplate {

    @Value("${certificate.file.advanced.path}")
    private String advancedCertificatePath;

    @Value("${user.certificate.img.path}")
    private String userImgPath;

    private static final String templatePath = "classpath:config/certificate-template/apku-advanced.pptx";
    private static final Logger logger = LoggerFactory.getLogger(AdvancedTemplate.class);
    private static final InputStream FILE_INPUT_STREAM_ADVANCED;

    static {
        InputStream FILE_INPUT_STREAM_ADVANCED1 = null;
        try {
            File file = ResourceUtils.getFile(templatePath);
            FILE_INPUT_STREAM_ADVANCED1 = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("模板文件没有找到！");
            FILE_INPUT_STREAM_ADVANCED1 = null;
        }
        FILE_INPUT_STREAM_ADVANCED = FILE_INPUT_STREAM_ADVANCED1;
    }

    @Override
    public void generateCertificateImg(CertificateInfo certificateInfo) throws IOException {
        logger.info("开始生成证书 certificateInfo = {}, advancedCertificatePath = {}", certificateInfo, advancedCertificatePath);
        // 生成证书名称
        String certName = MessageDigestUtil.getCertificateName(certificateInfo.getSerialNum());
        XMLSlideShow ppt = new XMLSlideShow(FILE_INPUT_STREAM_ADVANCED);
        XSLFSlide slide = ppt.getSlides().get(0);
        List<XSLFShape> shapeList = slide.getShapes();
        for (XSLFShape shape : shapeList) {
            if (shape instanceof XSLFTextBox) {
                XSLFTextBox xslfTextBox = (XSLFTextBox) shape;
                XSLFTextRun xslfTextRun = xslfTextBox.getTextParagraphs().get(0).getTextRuns().get(0);
                XmlObject xmlObject = xslfTextRun.getXmlObject();
                if (shape.getShapeName().equals(PlaceHolderName.userName.name())) {
                    ((CTRegularTextRunImpl) xmlObject).setT(certificateInfo.getUserName());
                } else if (shape.getShapeName().equals(PlaceHolderName.issueTime.name())) {
                    ((CTRegularTextRunImpl) xmlObject).setT(certificateInfo.getIssueTime());
                } else if (shape.getShapeName().equals(PlaceHolderName.serialNum.name())) {
                    ((CTRegularTextRunImpl) xmlObject).setT(certificateInfo.getSerialNum());
                }
            } else if (shape instanceof XSLFPictureShape) {
                XSLFPictureShape xslfPictureShape = (XSLFPictureShape) shape;
                if(shape.getShapeName().equals(PlaceHolderName.qrCode.name())){
                    try {
                        BufferedImage bufferedImage = QrCodeUtil.getQrCodeStream(super.getBaseUrl() + "/cert/" + certificateInfo.getSerialNum());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, Constant.CERTIFICATE_HEAD_IMG_TYPE, byteArrayOutputStream);
                        xslfPictureShape.getPictureData().setData(byteArrayOutputStream.toByteArray());
                    } catch (WriterException e) {
                        logger.error("证书上二维码生成错误！", e);
                    }
                }else if(shape.getShapeName().equals(PlaceHolderName.certificateImg.name())){
                    String fileName = certificateInfo.getIdCard() + Constant.DOT + Constant.CERTIFICATE_HEAD_IMG_TYPE;
                    File userImg = ResourceUtils.getFile(userImgPath + fileName);
                    xslfPictureShape.getPictureData().setData(IOUtils.toByteArray(new FileInputStream(userImg)));
                }
            }
        }
        // 保存目标 pptx 文件
        //ppt.write(new FileOutputStream(advancedCertificatePath + "generited.pptx"));
        // 保存图片
        Dimension onePPTPageSize = ppt.getPageSize();
        int times = 5;
        BufferedImage certificateImg = new BufferedImage(onePPTPageSize.width * times,
                onePPTPageSize.height * times, BufferedImage.TYPE_INT_RGB);
        Graphics2D oneGraphics2D = certificateImg.createGraphics();
        // 设置转换后的图片背景色为白色
        oneGraphics2D.setPaint(Color.white);
        // 将图片放大times倍
        oneGraphics2D.scale(times, times);
        oneGraphics2D.fill(new Rectangle2D.Float(0, 0, onePPTPageSize.width * times, onePPTPageSize.height * times));
        slide.draw(oneGraphics2D);
        String fileName = certName + Constant.DOT + Constant.CERTIFICATE_IMG_TYPE;
        File file = new File(advancedCertificatePath + fileName);
        ImageIO.write(certificateImg, Constant.CERTIFICATE_IMG_TYPE, file);

        logger.info("结束生成证书 certName ={}", certName);
    }


}
