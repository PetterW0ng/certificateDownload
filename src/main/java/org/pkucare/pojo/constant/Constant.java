package org.pkucare.pojo.constant;

/**
 * 常量类
 * Created by weiqin on 2019/9/23.
 */
public class Constant {

    /**
     * 缓存的名称同 ehcache.xml 对应的
     * certificatesURL 根据 手机号 获取 证书名称
     */
    public static final String EHCACHE_CERTIFICATE_URL = "certificatesURL";
    /**
     * certificatesInfo 根据 证书编号 获取 证书相关信息
     */
    public static final String EHCACHE_CERTIFICATES_INFO = "certificatesInfo";
    /**
     * verificationCodeCache 验证码 缓存
     */
    public static final String EHCACHE_VERIFICATION_CODE_CACHE = "verificationCodeCache";
    /**
     * 资源文件路径
     */
    public static final String ASSETS_PATH = "certificate.file.config.path";
    /**
     * 证书文件类型
     */
    public static final String FILE_TYPE = ".jpg";
    /**
     * 阿里大鱼 短信发送签名
     */
//    public static final String DAYU_SIGN_PRODUCT = "产品名称";
    public static final String DAYU_SIGN_PRODUCT = "北医脑健康";

}
