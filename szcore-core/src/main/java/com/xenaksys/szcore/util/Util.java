package com.xenaksys.szcore.util;


import com.xenaksys.szcore.Consts;

public class Util {

    public static String removeLastChar(String value){
        if(value == null || value.length() < 1){
            return value;
        }
        return value.substring(0, value.length() - 1);
    }

    public static String removeEndComma(String value){
        if(value == null){
            return value;
        }

        if(value.endsWith(Consts.COMMA)){
            return removeLastChar(value);
        }

        return value;
    }
}
