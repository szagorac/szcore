package com.xenaksys.szcore.util;

import static com.xenaksys.szcore.Consts.DOT;
import static com.xenaksys.szcore.Consts.DOT_CHAR;
import static com.xenaksys.szcore.Consts.FALSE;
import static com.xenaksys.szcore.Consts.MINUS_CHAR;
import static com.xenaksys.szcore.Consts.TRUE;

public class ParseUtil {

    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        } else {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (i == 0 && MINUS_CHAR == cs.charAt(i)) {
                    continue;
                }
                if (DOT_CHAR == cs.charAt(i)) {
                    continue;
                }
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isNumericSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        } else {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isBoolean(String value) {
        if (value == null) {
            return false;
        }
        String v = value.toLowerCase();
        return TRUE.equals(v) || FALSE.equals(v);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static Object parseWholeNumber(String val) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException numberFormatException) {
            return Long.valueOf(val);
        }
    }

    public static Object convertToType(String val) {
        if (isNumeric(val)) {
            if (val.contains(DOT)) {
                return Double.parseDouble(val);
            } else {
                return parseWholeNumber(val);
            }
        } else if (ParseUtil.isBoolean(val)) {
            return Boolean.parseBoolean(val);
        }
        return val;
    }
}

