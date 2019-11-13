package com.xenaksys.szcore.font;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xenaksys.szcore.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontLoader {

    static final Logger LOG = LoggerFactory.getLogger(FontLoader.class);

    static final String GLYPH_STR = "glyph";
    static final String GLYPH_LINE_START = "<" + GLYPH_STR;
    static final String PATH_TOKEN = "$PATH$";
    static final String CODE_POINT_TOKEN = "$CODE_POINT$";
    static final String IS_COMPOSITE_TOKEN = "$IS_COMPOSITE$";
    static final String INT_CODE_POINT_TOKEN = "$INT_CODE_POINT$";
    static final String GLYPH_NAME_TOKEN = "$GLYPH_NAME$";
    static final String GLYPH_DESCRPITION_TOKEN = "$GLYPH_DESCRPITION_TOKEN$";
    static final int SVG_WIDTH = 2000;
    static final int SVG_HEIGHT = 2000;
    static final double SVG_SCALE_X = 0.1;
    static final double SVG_SCALE_Y = -0.1;
    static final int SVG_TRANSLATE_X = 250;
    static final int SVG_TRANSLATE_Y = -1550;
    static final String UNICODE_STR = "unicode=\"";
    static final String HTML_VAL = "&#";
    static final String UNICODE_PREFIX = "U+";
    static final String HTML_HEX_STR = HTML_VAL + "x";
    static final String SEMI_COLUMN = ";";
    static final String SVG_EXT = ".svg";
    static final String SZ_NAME_SPACE_URI = "urn:xenaksys:szcore:glyph";

//    static final String OUT_DIR = "/MyHome/Music/phd/svg/bravura_export/";
    static final String OUT_DIR = "/Volumes/DataDrive/Music/phd/svg/font/bravura/bravura_export/";


    static final String SVG_FILE_TEMPLATE = "<svg " +
            " xmlns:svg=\"http://www.w3.org/2000/svg\"" +
            " xmlns=\"http://www.w3.org/2000/svg\"" +
            " width=\"" + SVG_WIDTH + "px\"" +
            " height=\"" + SVG_HEIGHT + "px\"" +
            " version=\"1.1\">" +
            " <szcore:glyphMeta xmlns:szcore=\""+ SZ_NAME_SPACE_URI + "\"" +
            " isComposite=\"" + IS_COMPOSITE_TOKEN + "\"" +
            " codePoint=\"" + CODE_POINT_TOKEN + "\"" +
            " intCodePoint=\"" + INT_CODE_POINT_TOKEN + "\"" +
            " glyphName=\"" + GLYPH_NAME_TOKEN + "\"" +
            " glyphDescription=\"" + GLYPH_DESCRPITION_TOKEN + "\"" +
            " />" +
            PATH_TOKEN +
            "</svg>";

    static final String PATH_ATTRIBS = "path transform=\"scale("+ SVG_SCALE_X + ", "+ SVG_SCALE_Y + ") translate("+ SVG_TRANSLATE_X + ", "+ SVG_TRANSLATE_Y + ")\"";

    static final Map<String, SmuflGlyphMeta> glyphIdMeta = new HashMap<>();


    static void parse(String path) throws Exception {
        List<String> lines = FileUtil.loadLinesFromFile(path);
        parseLines(lines);
    }


    private static void parseGlyphNames(String glyphNamesPath) {
        try {
            File namesFile = FileUtil.loadFile(glyphNamesPath);
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, SmuflGlyphMeta>>(){}.getType();
            Map<String, SmuflGlyphMeta> glyphsMetaMap = gson.fromJson(new FileReader(namesFile.getCanonicalPath()), type);

            for(String name : glyphsMetaMap.keySet()) {
                SmuflGlyphMeta meta = glyphsMetaMap.get(name);
                meta.setGlyphName(name);
                glyphIdMeta.put(meta.getCodepoint(),meta);
            }
            glyphIdMeta.size();

        } catch (Exception e) {
            LOG.error("Failed to process file: " + glyphNamesPath);
        }
    }

    static void parseLines(List<String> lines) throws Exception {
        if (lines == null || lines.isEmpty()) {
            return;
        }
     for (String line : lines) {
            if(!line.startsWith(GLYPH_LINE_START)) {
                continue;
            }

         String svgRawGlyphContent = parseGlyph(line);

         String svgContent = processSvgContent(svgRawGlyphContent);
         if(svgContent == null) {
             continue;
         }

         LOG.info(svgContent);

         createFile(svgContent);
        }
    }

    private static String processSvgContent(String svgContent) {
        String uniCodeValues = getUnicodeValue(svgContent);
        if(uniCodeValues == null || uniCodeValues.isEmpty()){
            LOG.error("Invalid unicode value : " + uniCodeValues);
            return null;
        }
        String[] unicodes = uniCodeValues.split(SEMI_COLUMN);
        String[] codePoints = new String[unicodes.length];
        String[] intCodePoints = new String[unicodes.length];
        String[] glyphNames = new String[unicodes.length];
        String[] glyphDescriptions = new String[unicodes.length];
        SmuflGlyphMeta[] metas = new SmuflGlyphMeta[unicodes.length];

        for(int i = 0; i < unicodes.length; i++){
            String unicode = unicodes[i];
            String hexValue = getHexValue(unicode);
            int intCodePoint = getIntCodePoint(hexValue);
            intCodePoints[i] = getHtmlIntCodePoint(intCodePoint);
            String codePoint = getCodePoint(hexValue);
            codePoints[i] = codePoint;
            SmuflGlyphMeta meta = glyphIdMeta.get(codePoint);
            if(meta == null) {
                metas[i] = null;
                glyphNames[i] = codePoint;
                glyphDescriptions[i] = codePoint;
            } else {
                metas[i] = meta;
                glyphNames[i] = meta.getGlyphName();
                glyphDescriptions[i] = meta.getDescription();
            }
        }

        String cps = String.join(";", codePoints);
        String icps = String.join(";", intCodePoints);
        String names = String.join(";", glyphNames);
        String descripts = String.join(";", glyphDescriptions);
        Boolean isComposite = codePoints.length > 1;

        svgContent = svgContent.replace(CODE_POINT_TOKEN, cps)
                .replace(INT_CODE_POINT_TOKEN, icps)
                .replace(GLYPH_NAME_TOKEN, names)
                .replace(IS_COMPOSITE_TOKEN, isComposite.toString())
                .replace(GLYPH_DESCRPITION_TOKEN, descripts);

        return svgContent;
    }


    private static String getUnicodeValue(String svgContent) {
        //        char c = "\uFFFF".toCharArray()[0];
        int unicodeStart = svgContent.indexOf(UNICODE_STR);
        if(unicodeStart <= 0) {
            LOG.info("Invalid Unicode");
            return null;
        }

        int startIndex = unicodeStart+UNICODE_STR.length();
        int endIndex = svgContent.indexOf('"', startIndex);
        return svgContent.substring(startIndex, endIndex);
    }

    private static void createFile(String svgContent) {
        //        char c = "\uFFFF".toCharArray()[0];

        String fileName = getFileName(svgContent);

        FileUtil.writeToFile(svgContent, OUT_DIR + fileName + SVG_EXT);
        LOG.info(fileName);
    }

    private static String getFileName(String svContent) {
        try {
            byte[] byteArray = svContent.getBytes("UTF-8");
            ByteArrayInputStream inputStream;
            inputStream = new ByteArrayInputStream(byteArray);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            while(reader.hasNext()){
                int event = reader.next();
                switch(event){
                    case XMLStreamConstants.START_ELEMENT:
                        String namespaceURI = reader.getNamespaceURI();
                        if(SZ_NAME_SPACE_URI.equals(namespaceURI)) {
                            String localName = reader.getLocalName();
                            LOG.info("Have my element " + localName);
                            if ("glyphMeta".equals(localName)){
                                int count = reader.getAttributeCount();
                                for(int i = 0; i < count; i++) {
                                    QName att = reader.getAttributeName(i);
                                    String localPart = att.getLocalPart();
                                    if(localPart.equals("glyphName")) {
                                        String val = reader.getAttributeValue(i);
                                        return val;
                                    }
                                }
                            }
                        }

                        break;
                    case XMLStreamConstants.CHARACTERS:
                        String tagContent = reader.getText().trim();
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        switch(reader.getLocalName()){
                            case "szcore":
                                break;
                        }
                        break;
                    case XMLStreamConstants.START_DOCUMENT:
                        break;
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to process svgContent", e);
        }

        return "unknown";
    }

    private static String parseGlyph(String line) {
        String svgPath = line.replace(GLYPH_STR, PATH_ATTRIBS);
        return SVG_FILE_TEMPLATE.replace(PATH_TOKEN, svgPath);
    }

    private static String newString(String hexStr) {
        int codePoint = getIntCodePoint(hexStr);
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf((char) codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    public static String convertUnicodeToIntStr(String hexStr) {
        if(hexStr == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String[] vals = hexStr.split(SEMI_COLUMN);
        for(String val : vals) {
            int codePoint = getIntCodePoint(val);
            sb.append(HTML_VAL);
            sb.append(codePoint);
            sb.append(SEMI_COLUMN);
        }

        return sb.toString();
    }


    public static int getIntCodePoint(String hexStr) {
        hexStr = hexStr.replace(" ", "");

        if(hexStr.isEmpty()) {
            return 0;
        }

        if(hexStr.startsWith(HTML_HEX_STR)) {
            hexStr = hexStr.replaceAll(HTML_HEX_STR,"");
        }

        return Integer.parseInt(hexStr, 16);
    }

    public static String getHexValue(String hexStr) {
        hexStr = hexStr.replace(" ", "");

        if(hexStr.startsWith(HTML_HEX_STR)) {
            hexStr = hexStr.replaceAll(HTML_HEX_STR,"");
        }

        return hexStr;
    }

    public static String getCodePoint(String hexVal) {
        return UNICODE_PREFIX + hexVal.toUpperCase();
    }

    public static String getHtmlIntCodePoint(int intVal) {
        return UNICODE_PREFIX + intVal;
    }

    public static void main(String [ ] args){
        String fontPath = "font/Bravura.svg";
        String glyphNamesPath = "font/glyphnames.json";
        try {
            parseGlyphNames(glyphNamesPath);
            parse(fontPath);
        } catch (Exception e) {
            LOG.error("Failed to process file " + fontPath, e);
        }
    }


}
