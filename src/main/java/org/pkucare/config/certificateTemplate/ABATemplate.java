package org.pkucare.config.certificateTemplate;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.impl.CTRegularTextRunImpl;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@Component("abaTemplate")
public class ABATemplate extends CertificateTemplate {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedTemplate.class);

    @Value("${certificate.file.aba.path}")
    private String abaCertificatePath;

    private static final String templatePath = "config/certificate-template/aba.pptx";
    private static final byte[] FILE_INPUT_STREAM_ADVANCED_ARRAY;

    static {
        byte[] temp = null;
        try {
            InputStream fileInputStream = new ClassPathResource(templatePath).getInputStream();
            temp = IOUtils.toByteArray(fileInputStream);
        } catch (FileNotFoundException e) {
            logger.error("模板文件没有找到！", e);
        } catch (IOException e) {
            logger.error("读取 {} 模板文件失败了！");
        }
        FILE_INPUT_STREAM_ADVANCED_ARRAY = temp;
    }


    @Override
    @Async
    protected void generateCertificateImg(CertificateInfo certificateInfo) throws IOException {
        logger.info("开始生成证书 certificateInfo = {}, CertificatePath = {}", certificateInfo, abaCertificatePath);
        // 生成证书名称
        String certName = certificateInfo.getFileName();
        XMLSlideShow ppt = new XMLSlideShow(new ByteArrayInputStream(FILE_INPUT_STREAM_ADVANCED_ARRAY));
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
                    ((CTRegularTextRunImpl) xmlObject).setT("发证日期：" + certificateInfo.getIssueTime());
                } else if (shape.getShapeName().equals(PlaceHolderName.serialNum.name())) {
                    ((CTRegularTextRunImpl) xmlObject).setT(certificateInfo.getSerialNum());
                }
            }
        }
        generateJPGFile(ppt, certName);
        logger.info("结束生成证书 certName ={}", certName);
    }

    private void generateJPGFile(XMLSlideShow ppt, String certName) throws IOException {
        XSLFSlide slide = ppt.getSlides().get(0);
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
        String fileName = certName + Constant.DOT + Constant.CERTIFICATE_IMG_TYPE_JPG;
        File file = new File(abaCertificatePath + fileName);
        ImageIO.write(certificateImg, Constant.CERTIFICATE_IMG_TYPE_JPG, file);
        logger.info("证书图片生成成功！");
    }
}
