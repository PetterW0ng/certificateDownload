package org.pkucare.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QrCodeUtil {

    private static final String logoPath = "config/certificate-template/logo.jpg";
    /**
     * 生成二维码
     */
    public static void QREncode(String content) throws WriterException, IOException {
        int width = 300; // 图像宽度
        int height = 300; // 图像高度
        String format = "png";// 图像类型
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToPath(bitMatrix, format, new File("C:\\data\\qrcode1.png").toPath());// 输出原图片
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        BufferedImage bufferedImage = LogoMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig));
//        BufferedImage bufferedImage = LogoMatrix(toBufferedImage(bitMatrix), new File("D:\\logo.png"));
        ImageIO.write(bufferedImage, "png", new File("C:\\data\\qrCode.png"));//输出带logo图片
    }

    public static BufferedImage getQrCodeStream(String content) throws WriterException, IOException {
        int width = 300;
        int height = 300;
        Map<EncodeHintType, Object> hints = new HashMap<>(3);
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        BufferedImage bufferedImage = LogoMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig));
        return bufferedImage;
    }

    /**
     * 二维码添加logo
     *
     * @param matrixImage 源二维码图片
     * @return 返回带有logo的二维码图片
     * 参考：https://blog.csdn.net/weixin_39494923/article/details/79058799
     */
    private static BufferedImage LogoMatrix(BufferedImage matrixImage) throws IOException {
        /**
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        /**
         * 读取Logo图片 文件流
         */
        InputStream logoFileInputStream = new ClassPathResource(logoPath).getInputStream();
        BufferedImage logo = ImageIO.read(logoFileInputStream);

        //开始绘制图片
        g2.drawImage(logo, matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, null);//绘制
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, 20, 20);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth / 5 * 2 + 2, matrixHeigh / 5 * 2 + 2, matrixWidth / 5 - 4, matrixHeigh / 5 - 4, 20, 20);
        g2.setColor(new Color(128, 128, 128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        matrixImage.flush();
        return matrixImage;
    }

    public static void main(String[] args) {
        try {
            QREncode("你好 QRCODE");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
