package com.zy.myblog.xx.utils;

/**
 * @author zy 1716457206@qq.com
 */
public class StringUtils {

    /** @Author zy
     * @Description 判断字符串是否为空
     * @Param [s]
     * @return java.lang.Boolean
     **/
    public static Boolean isEmpty(String s) {

        if (s == null || s.length() <= 0) {
            return true;
        }
        return false;
    }

}
