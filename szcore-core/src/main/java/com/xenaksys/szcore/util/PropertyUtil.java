package com.xenaksys.szcore.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class PropertyUtil {
    private static Logger LOG = LoggerFactory.getLogger(PropertyUtil.class);

    public static String parsePropertyValue(String propName, String defaultValue, Properties props) {
        String value = props.getProperty(propName);
        if (value == null) {
            LOG.warn("Could not find valid " + propName + " property, setting: " + defaultValue);
            value = defaultValue;
        }
        return value;
    }

    public static long parseLongPropertyValue(String propName, long defaultValue, Properties props) {
        String value = props.getProperty(propName);
        long out = defaultValue;
        if (value == null) {
            LOG.warn("Could not find valid " + propName + " property, setting: " + defaultValue);
            return defaultValue;
        }

        try {
            out = Long.valueOf(value);
        } catch (NumberFormatException e) {
            LOG.error("Could not parse " + propName + " property, setting: " + defaultValue, e);
            return defaultValue;
        }

        return out;
    }

    public static boolean parseBooleanPropertyValue(String propName, boolean defaultValue, Properties props) {
        String value = props.getProperty(propName);
        boolean out = defaultValue;
        if (value == null) {
            LOG.warn("Could not find valid " + propName + " property, setting: " + defaultValue);
            return defaultValue;
        }

        try {
            out = Boolean.valueOf(value);
        } catch (NumberFormatException e) {
            LOG.error("Could not parse " + propName + " property, setting: " + defaultValue, e);
            return defaultValue;
        }

        return out;
    }

}
