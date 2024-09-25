package com.bond.allianz.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class GuidUtil {

    /**
     * 生成guid
     * @return
     */
    public static  String newGuid(){
        return  UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
    }

    /**
     * 随机盐值生成
     * @return
     */
    public  static  String newSalt(){
        SecureRandom randow=new SecureRandom();
        return String.valueOf(randow.nextInt());
    }
}
