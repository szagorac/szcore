package com.xenaksys.szcore.score;

import com.xenaksys.szcore.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlLoader {
    static final Logger LOG = LoggerFactory.getLogger(YamlLoader.class);

    public static Map<String, Object> loadYaml(File file) throws Exception {
        String doc = FileUtil.readYamlFromFile(file);
        Yaml yaml = new Yaml();
        return yaml.load(doc);
    }

    public static List<Map<String, Object>> getListOfMaps(String key, Map<String, Object> configMap) {
        List<Object> objList = getList(key, configMap);
        return convertToListOfMaps(objList);
    }

    public static List<Map<String, Object>> convertToListOfMaps(List<Object> objList) {
        List<Map<String, Object>> listOfMaps = new ArrayList<>();
        if (objList == null) {
            return listOfMaps;
        }

        for (Object obj : objList) {
            if (obj instanceof Map) {
                listOfMaps.add((Map<String, Object>) obj);
            } else {
                LOG.warn("Unexpected element in object list {}, expected map", obj);
            }
        }

        return listOfMaps;
    }

    public static List<Object> getList(String key, Map<String, Object> configMap) {
        Object val = configMap.get(key);
        if (val == null) {
            return null;
        }

        if (!(val instanceof List)) {
            LOG.warn("Unexpected type for map value, key {}, value {}", key, val);
            return null;
        }

        return (List<Object>) val;
    }

    public static String getString(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            LOG.warn("Could not find string value for key {}", key);
            return null;
        }

        if (!(val instanceof String)) {
            LOG.warn("Unexpected type for string value, key {}, value {}", key, val);
            return null;
        }

        return (String) val;
    }

    public static Map<String, Object> getMap(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            return null;
        }

        if (!(val instanceof Map)) {
            LOG.warn("Unexpected type for map value, key {}, value {}", key, val);
            return null;
        }

        return (Map<String, Object>) val;
    }

    public static Integer getInteger(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            LOG.warn("Could not find integer value for key {}", key);
            return null;
        }

        if (!(val instanceof Integer)) {
            LOG.warn("Unexpected type for integer value, key {}, value {}", key, val);
            return null;
        }

        return (Integer) val;
    }

    public static Double getDouble(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            LOG.warn("Could not find double value for key {}", key);
            return null;
        }

        if (val instanceof Double) {
            return (Double) val;
        } else if(val instanceof String) {
            return Double.valueOf((String)val);
        } else if(val instanceof Number) {
            return ((Number) val).doubleValue();
        }

        return null;
    }

    public static Boolean getBoolean(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            LOG.warn("Could not find boolean value for key {}", key);
            return null;
        }

        if (!(val instanceof Boolean)) {
            LOG.warn("Unexpected type for boolean value, key {}, value {}", key, val);
            return null;
        }

        return (Boolean) val;
    }

    public static List<String> getStrList(String key, Map<String, Object> scoreMap) {
        List<Object> ls = getList(key, scoreMap);
        if (ls == null) {
            return null;
        }

        ArrayList<String> out = new ArrayList<>();
        for (Object o : ls) {
            if (!(o instanceof String)) {
                out.add(String.valueOf(o));
            } else {
                out.add((String) o);
            }
        }
        return out;
    }
    public static List<List<String>> getListOfStrList(String key, Map<String, Object> scoreMap) {
        List<Object> ls = getList(key, scoreMap);
        if (ls == null) {
            return null;
        }

        List<List<String>> out = new ArrayList<>();
        for (Object o : ls) {
            if (!(o instanceof List)) {
                List<String> l = new ArrayList<>();
                l.add(String.valueOf(o));
                out.add(l);
            } else {
                out.add((List<String>)o);
            }
        }
        return out;
    }
}
