package org.pkucare.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 字符串 加密
 * Created by weiqin on 2019/6/17.
 */
public class MessageDigestUtil {

    private static final String key = "5O1WZZY916UC4JYQ";

    private static final String CERTIFICATE_KEY = "M3240YELWIVWW0LQ";

    /**
     * 根据字符串生成 md5 值
     *
     * @param value
     * @return
     */
    public static String md5(String value) {

        MessageDigest md = null;
        try {
            value = value + key;
            md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new BigInteger(1, md.digest()).toString(16).toUpperCase();
    }

    public static String getCertificateName(String seriNum) {

        MessageDigest md = null;
        try {
            seriNum = seriNum + CERTIFICATE_KEY;
            md = MessageDigest.getInstance("MD5");
            md.update(seriNum.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new BigInteger(1, md.digest()).toString(16).toUpperCase();
    }

}
