package com.xenaksys.szcore.web;

import com.xenaksys.szcore.model.ZsResponseType;

public class ZsWebResponse {

    private final ZsResponseType type;
    private final String data;

    public ZsWebResponse(ZsResponseType type, String data) {
        this.type = type;
        this.data = data;
    }

    public ZsResponseType getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
