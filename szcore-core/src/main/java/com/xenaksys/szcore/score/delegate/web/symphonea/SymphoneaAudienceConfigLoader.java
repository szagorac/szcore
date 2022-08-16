package com.xenaksys.szcore.score.delegate.web.symphonea;

import com.xenaksys.szcore.score.web.audience.config.AudienceWebscoreConfig;
import com.xenaksys.szcore.score.web.audience.config.AudienceWebscoreConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SymphoneaAudienceConfigLoader extends AudienceWebscoreConfigLoader {
    static final Logger LOG = LoggerFactory.getLogger(SymphoneaAudienceConfigLoader.class);

    public SymphoneaAudienceWebscoreConfig load(String workingDir) throws Exception {
        SymphoneaAudienceWebscoreConfig config = new SymphoneaAudienceWebscoreConfig();
        load(workingDir, config);
        return config;
    }

    @Override
    public void loadDelegate(AudienceWebscoreConfig parent, Map<String, Object> configMap) {
        if (!(parent instanceof SymphoneaAudienceWebscoreConfig)) {
            return;
        }
        SymphoneaAudienceWebscoreConfig config = (SymphoneaAudienceWebscoreConfig) parent;
    }

}
