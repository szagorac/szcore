package com.xenaksys.szcore.util;

import com.xenaksys.szcore.web.ZsHttpResponse;

public class HttpUtil {

    public static String getMimeType(ZsHttpResponse response) {
        switch (response.getType()) {
            case JSON:
                return "application/json";
            case XML:
                return "application/xml";
            case HTML:
                return "text/html";
            case TEXT:
            default:
                return "text/plain";
        }
    }
}
