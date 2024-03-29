package com.xenaksys.szcore.web;

import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;

public interface WebAudienceStateListener {

    void onWebAudienceScoreStateChange(WebAudienceScoreStateExport webAudienceScoreStateExport);

    void onWebAudienceScoreStateDeltaChange(WebAudienceScoreStateDeltaExport webAudienceScoreStateDeltaExport);

}
