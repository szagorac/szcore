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

    public static int getStringLengthUtf8(CharSequence cs) {
        return cs.codePoints()
                .map(cp -> cp<=0x7ff? cp<=0x7f? 1: 2: cp<=0xffff? 3: 4)
                .sum();
    }

    public static int getStringLengthUtf8Ascii(CharSequence cs) {
        return cs.length()
                + cs.codePoints().filter(cp -> cp>0x7f).map(cp -> cp<=0x7ff? 1: 2).sum();
    }
}
