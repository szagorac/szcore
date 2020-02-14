package com.xenaksys.szcore.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebDataContainer {

    private final Map<String, String> dataBag = new ConcurrentHashMap<>();

    public void addToDataBag(String name, String value) {
        dataBag.put(name, value);
    }

    public String getFromDataBag(String name) {
        return dataBag.get(name);
    }

    public Map<String, String> getDataBag() {
        return dataBag;
    }
}
