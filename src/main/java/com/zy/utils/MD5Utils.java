package com.zy.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    public static String MD5Digest(String source){

        String md5Hex = DigestUtils.md5Hex(source);

        return  md5Hex;
    }

    public  static String MD5Digest(String source,Integer salt){

        char[] chars = source.toCharArray();

        for (int i=0; i<chars.length;i++){
            chars[i]=  (char) (chars[i]+salt);
        }

         String finalSource=new String(chars);

        String md5Hex = DigestUtils.md5Hex(finalSource);

        return md5Hex;
    }


}
