package com.cleverua.fastmap.utils;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 12:25 PM
 * Email: akulakovsky@cleverua.com
 */
public class StringUtils {

    public static String getBinaryString(int value, int length){
        String str = Integer.toBinaryString(value);
        str = appendToString(0, str, "0", length);
        return str;
    }

    public static String appendToString(int position,String str, String what, int finalLength){
        StringBuilder builder = new StringBuilder(str);
        while (builder.length() < finalLength){
            builder.insert(position, what);
        }
        return builder.toString();
    }

}
