package com.xenaksys.szcore.score.delegate.web.dialogs;

import com.xenaksys.szcore.score.web.audience.config.AudienceWebscoreConfig;
import com.xenaksys.szcore.score.web.audience.config.AudienceWebscoreConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DialogsAudienceConfigLoader extends AudienceWebscoreConfigLoader {
    static final Logger LOG = LoggerFactory.getLogger(DialogsAudienceConfigLoader.class);

    public DialogsAudienceWebscoreConfig load(String workingDir) throws Exception {
        DialogsAudienceWebscoreConfig config = new DialogsAudienceWebscoreConfig();
        load(workingDir, config);
        return config;
    }

    @Override
    public void loadDelegate(AudienceWebscoreConfig parent, Map<String, Object> configMap) {
        if (!(parent instanceof DialogsAudienceWebscoreConfig)) {
            return;
        }
        DialogsAudienceWebscoreConfig config = (DialogsAudienceWebscoreConfig) parent;
    }

}
