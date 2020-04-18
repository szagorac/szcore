package com.xenaksys.szcore.web;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZsHttpRequest {
    private final Map<String, String> stringParams = new HashMap<>();
    private List<Path> filePaths = null;

    private final String requestPath;
    private final String sourceAddr;

    public ZsHttpRequest(String requestPath, String sourceAddr) {
        this.requestPath = requestPath;
        this.sourceAddr = sourceAddr;
    }

    public void addStringParam(String name, String value) {
        if(name == null) {
            return;
        }
        stringParams.put(name, value);
    }

    public void addFilePath(Path path) {
        if(path == null) {
            return;
        }
        if(filePaths == null) {
            filePaths = new ArrayList<>(1);
        }
        filePaths.add(path);
    }

    public Map<String, String> getStringParams() {
        return stringParams;
    }

    public String getParam(String name) {
        return stringParams.get(name);
    }

    public boolean containsParam(String name) {
        return stringParams.containsKey(name);
    }

    public List<Path> getFilePaths() {
        return filePaths;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }
}
