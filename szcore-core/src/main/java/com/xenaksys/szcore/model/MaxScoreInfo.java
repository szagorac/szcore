package com.xenaksys.szcore.model;

import com.xenaksys.szcore.algo.config.StrategyConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.xenaksys.szcore.Consts.CONFIG_BUFFER1;
import static com.xenaksys.szcore.Consts.CONFIG_BUFFER2;
import static com.xenaksys.szcore.Consts.CONFIG_BUFFER3;
import static com.xenaksys.szcore.Consts.CONFIG_BUFFER4;
import static com.xenaksys.szcore.Consts.CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.CONFIG_GROOVE;

public class MaxScoreInfo extends ExtScoreInfo {

    static final Logger LOG = LoggerFactory.getLogger(StrategyConfigLoader.class);

    private String buffer1File;
    private String buffer2File;
    private String buffer3File;
    private String buffer4File;
    private String granulatorFile;
    private String grooveFile;

    public MaxScoreInfo(String id) {
        super(id);
    }

    public String getBuffer1File() {
        return buffer1File;
    }

    public void setBuffer1File(String buffer1File) {
        this.buffer1File = buffer1File;
    }

    public String getBuffer2File() {
        return buffer2File;
    }

    public void setBuffer2File(String buffer2File) {
        this.buffer2File = buffer2File;
    }

    public String getBuffer3File() {
        return buffer3File;
    }

    public void setBuffer3File(String buffer3File) {
        this.buffer3File = buffer3File;
    }

    public String getBuffer4File() {
        return buffer4File;
    }

    public void setBuffer4File(String buffer4File) {
        this.buffer4File = buffer4File;
    }

    public String getGranulatorFile() {
        return granulatorFile;
    }

    public void setGranulatorFile(String granulatorFile) {
        this.granulatorFile = granulatorFile;
    }

    public String getGrooveFile() {
        return grooveFile;
    }

    public void setGrooveFile(String grooveFile) {
        this.grooveFile = grooveFile;
    }

    public void setTargetValues(Map<String, String> targetValues) {
        super.setTargetValues(targetValues);
        for(String target : targetValues.keySet()) {
            String value = targetValues.get(target);
            setTargetValue(target, value);
        }
    }

    public void setTargetValue(String target, String value) {
        if(target == null || value == null) {
            return;
        }

        switch (target) {
            case CONFIG_BUFFER1:
                setBuffer1File(value);
                break;
            case CONFIG_BUFFER2:
                setBuffer2File(value);
                break;
            case CONFIG_BUFFER3:
                setBuffer3File(value);
                break;
            case CONFIG_BUFFER4:
                setBuffer4File(value);
                break;
            case CONFIG_GRANULATOR:
                setGranulatorFile(value);
                break;
            case CONFIG_GROOVE:
                setGrooveFile(value);
                break;
            default:
                LOG.error("setTargetValue: unknown target: {}", target);

        }
    }
}
