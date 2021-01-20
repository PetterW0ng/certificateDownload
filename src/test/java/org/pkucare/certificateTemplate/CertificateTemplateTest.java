package org.pkucare.certificateTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.openxmlformats.schemas.drawingml.x2006.main.impl.CTRegularTextRunImpl;
import org.pkucare.SpringBootApplicationTest;
import org.pkucare.config.certificateTemplate.CertificateTemplate;
import org.pkucare.pojo.CertificateInfo;
import org.pkucare.pojo.constant.Constant;
import org.pkucare.util.QrCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static javax.print.attribute.ResolutionSyntax.DPI;

public class CertificateTemplateTest extends SpringBootApplicationTest {
    private static final Logger logger = LoggerFactory.getLogger(CertificateTemplateTest.class);

    @Resource(name = "advancedTemplate")
    private CertificateTemplate advancedTemplate;


    @Test
    public void testGenerateCertificate(){
        String certJSON = "{ \n" +
                "    \"userName\" : \"扩木那\", \n" +
                "    \"serialNum\" : \"BISA-210404199602290023\", \n" +
                "    \"certificateName\" : \"ABA孤独症康复专业技能培训证书\", \n" +
                "    \"issueTime\" : \"2020.10.25\", \n" +
                "    \"idCard\" : \"350524197805190041\", \n" +
                "    \"issuingUnit\" : \"中国残疾人联合会社会服务指导中心、北医脑健康行为发展教研院\", \n" +
                "    \"fileName\" : \"CF6A1F8248B765F5576D0A7A5A630C34\", \n" +
                "    \"phone\" : \"13476140598\"\n" +
                "}";
        CertificateInfo certificateInfo = JSONObject.toJavaObject(JSON.parseObject(certJSON), CertificateInfo.class);
        advancedTemplate.generateCertificate(certificateInfo);
    }

    @Test
    public void testGeneratePng() throws IOException {
        String[] number = {"8y2FUNxa", "8y34zPqA", "8y4MjAdT", "8y4xVQ5r", "8y5Wkk6e", "8y5qXeHb", "8y6DMYbj", "8y6ZtQYS", "8y6eAt8T", "8y6rZDpS", "8y7Eyc35", "8y7UkECs"};
        for (String num : number){
            generateCertificateImg("礼品卡"+num, num);
        }

    }

    private static final byte[] FILE_INPUT_STREAM_ADVANCED_ARRAY;

    private static final String templatePath = "config/certificate-template/new.pptx";
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


    @Async
    public void generateCertificateImg(String pngName, String number) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow(new ByteArrayInputStream(FILE_INPUT_STREAM_ADVANCED_ARRAY));
        XSLFSlide slide = ppt.getSlides().get(0);
        List<XSLFShape> shapeList = slide.getShapes();
        for (XSLFShape shape : shapeList) {
            if (shape instanceof XSLFTextBox) {
                XSLFTextBox xslfTextBox = (XSLFTextBox) shape;
                XSLFTextRun xslfTextRun = xslfTextBox.getTextParagraphs().get(0).getTextRuns().get(0);
                XmlObject xmlObject = xslfTextRun.getXmlObject();
                if (shape.getShapeName().equals("number")){
                    ((CTRegularTextRunImpl) xmlObject).setT(number);
                }
            }
        }

        // 保存目标 pptx 文件
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 4);
        ppt.write(byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        byteArrayOutputStream = null;
        generateJPGFile(byteArrayInputStream, pngName);

        // 1111111111111111111
        logger.info("结束生成 certName ={}", pngName);
    }

    String tempDirectory = "C:/迅雷下载/png/";

    private void generateJPGFile(ByteArrayInputStream byteArrayInputStream, String certName) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow(byteArrayInputStream);
        XSLFSlide slide = ppt.getSlides().get(0);
        // 保存图片
        Dimension onePPTPageSize = ppt.getPageSize();

        /*ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
        IIOMetadata metadata = writer.getDefaultImageMetadata（typeSpecifier，writeParam）;*/
        int times = 2;
        BufferedImage certificateImg = new BufferedImage(onePPTPageSize.width * times,
                onePPTPageSize.height * times, BufferedImage.TYPE_INT_RGB);

        Graphics2D oneGraphics2D = certificateImg.createGraphics();
        // 设置转换后的图片背景色为白色
        oneGraphics2D.setPaint(Color.white);
        // 将图片放大times倍
        oneGraphics2D.scale(times, times);
        oneGraphics2D.fill(new Rectangle2D.Float(0, 0, onePPTPageSize.width * times, onePPTPageSize.height * times));
        slide.draw(oneGraphics2D);
        String fileName = certName + Constant.DOT + "jpeg";
        File file = new File(tempDirectory + fileName);

        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(new FileOutputStream(file));
        JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(certificateImg);
        jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
        jpegEncoder.setJPEGEncodeParam(jpegEncodeParam); jpegEncodeParam.setQuality(0.75f, false);
        jpegEncodeParam.setXDensity(300); jpegEncodeParam.setYDensity(300);
        jpegEncoder.encode(certificateImg, jpegEncodeParam);
        certificateImg.flush();
//        ImageIO.write(certificateImg, Constant.CERTIFICATE_IMG_TYPE_PNG, file);
        byteArrayInputStream = null;
        logger.info("图片生成成功！");
    }


    /**
     *
     BufferedImage image = ImageIO.read(new File(path));
     JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(new FileOutputStream(new File(path)));
     JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(image);
     jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
     jpegEncoder.setJPEGEncodeParam(jpegEncodeParam); jpegEncodeParam.setQuality(0.75f, false);
     jpegEncodeParam.setXDensity(300); jpegEncodeParam.setYDensity(300);
     jpegEncoder.encode(image, jpegEncodeParam);
     image.flush();
     */

    public static void saveGridImage(File output,BufferedImage gridImage) throws IOException {
        output.delete();

        final String formatName = "png";

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata);

            final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(gridImage, null, metadata), writeParam);
            } finally {
                stream.close();
            }
            break;
        }
    }

    public static void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

        // for PNG, it's dots per millimeter
        double dotsPerMilli = 1.0 * DPI / 10 / 2.54;
        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));
        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));
        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);
        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);
        metadata.mergeTree("javax_imageio_1.0", root);
    }
}
