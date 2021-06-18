package com.xenaksys.szcore.util;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.InstrumentId;

import java.util.List;

import static com.xenaksys.szcore.Consts.COMMA;
import static com.xenaksys.szcore.Consts.DOT;
import static com.xenaksys.szcore.Consts.DOT_CHAR;
import static com.xenaksys.szcore.Consts.EMPTY;
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

    public static String removeSlashes(String str) {
        return str.replaceAll(Consts.SLASH, EMPTY);
    }

    public static int getFirstDigitIndex(CharSequence cs) {
        if (isEmpty(cs)) {
            return -1;
        } else {
            int sz = cs.length();
            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return i;
                }
            }
            return -1;
        }
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
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

    public static String parseToken(String val, String token, String replaceWith) {
        if (!val.contains(token)) {
            return val;
        }
        return val.replaceAll(token, replaceWith);
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

    public static String convertToCsv(List<InstrumentId> instrumentIds) {
        StringBuilder csvBuilder = new StringBuilder();
        String delimiter = EMPTY;
        for (InstrumentId id : instrumentIds) {
            csvBuilder.append(delimiter);
            csvBuilder.append(id.getName());
            delimiter = COMMA;
        }
        return csvBuilder.toString();
    }
}

