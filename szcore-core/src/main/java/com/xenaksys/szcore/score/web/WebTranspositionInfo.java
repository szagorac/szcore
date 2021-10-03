package com.xenaksys.szcore.score.web;

import java.util.ArrayList;
import java.util.List;

public class WebTranspositionInfo {
    private List<WebTextinfo> txtInfos;

    public void addTxtInfo(WebTextinfo textinfo) {
        if (txtInfos == null) {
            txtInfos = new ArrayList<>();
        }
        txtInfos.add(textinfo);
    }

    public List<WebTextinfo> getTxtInfos() {
        return txtInfos;
    }

    public WebTextinfo getTxtInfo(int index) {
        if(txtInfos == null || index < 0 || index >= txtInfos.size()) {
            return null;
        }
        return txtInfos.get(index);
    }
}
