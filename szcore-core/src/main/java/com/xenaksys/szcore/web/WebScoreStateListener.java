package com.xenaksys.szcore.web;

import com.xenaksys.szcore.score.web.export.WebScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.export.WebScoreStateExport;

public interface WebScoreStateListener {

    void onWebScoreStateChange(WebScoreStateExport webScoreStateExport);

    void onWebScoreStateDeltaChange(WebScoreStateDeltaExport webScoreStateDeltaExport);

}
