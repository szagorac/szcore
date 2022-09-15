package com.xenaksys.szcore.score.web;

import java.util.ArrayList;
import java.util.List;

public class WebMovementInfo {
    private String name;
    private final List<String> parts = new ArrayList<>();
    private int startPage;
    private int endPage;
    private String imgPageNameToken;
    private String imgContPageName;
    private int contPageNo;
    private String imgDir;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        if(parts == null || parts.isEmpty()) {
            return;
        }
        this.parts.addAll(parts);
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public String getImgPageNameToken() {
        return imgPageNameToken;
    }

    public void setImgPageNameToken(String imgPageNameToken) {
        this.imgPageNameToken = imgPageNameToken;
    }

    public String getImgContPageName() {
        return imgContPageName;
    }

    public void setImgContPageName(String imgContPageName) {
        this.imgContPageName = imgContPageName;
    }

    public int getContPageNo() {
        return contPageNo;
    }

    public void setContPageNo(int contPageNo) {
        this.contPageNo = contPageNo;
    }

    public String getImgDir() {
        return imgDir;
    }

    public void setImgDir(String imgDir) {
        this.imgDir = imgDir;
    }
}
