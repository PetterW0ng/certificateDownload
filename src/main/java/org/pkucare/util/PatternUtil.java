package org.pkucare.util;

import java.util.regex.Pattern;

/**
 * Created by weiqin on 2019/6/14.
 */
public class PatternUtil {

    /**
     * 手机号正则 验证器
     */
    private static Pattern phonePatter = Pattern.compile("^1(3|4|5|6|7|8|9)\\d{9}$");

    public static boolean testPhone(String phone){
        return phonePatter.matcher(phone).matches();
    }
}
