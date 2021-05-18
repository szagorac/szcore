package com.xenaksys.szcore.score;

import com.xenaksys.szcore.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SvgColourReplacer {
    static final Logger LOG = LoggerFactory.getLogger(SvgColourReplacer.class);

    private final static String FILE_NAME = "outerCircle7.svg";
//    private final static String FILE_NAME = "CentreShape.svg";
//    private final static String FILE_NAME = "innerCircle1.svg";
//    private final static String FILE_NAME = "outerCircle1.svg";

    private final static String DIR_PATH = "C:\\dev\\projects\\github\\scores\\ligetiq\\export\\web\\img\\";
    private final static String FILE_PATH = DIR_PATH + FILE_NAME;
    private final static String CLS = ".cls-";
    private final static String FILL = "fill:";

    private final static Map<String, String> colMap = new HashMap<>();
    static{
        colMap.put("#191835", ".clsWindow-1");
        colMap.put("#29586a", ".clsWindow-2");
        colMap.put("#94928d", ".clsWindow-3");
        colMap.put("#595337", ".clsWindow-4");
        colMap.put("#cacbad", ".clsWindow-5");
        colMap.put("#866a58", ".clsWindow-6");
        colMap.put("#cd3412", ".clsWindow-7");
        colMap.put("#999373", ".clsWindow-8");
        colMap.put("#5d4430", ".clsWindow-9");
        colMap.put("#beaf64", ".clsWindow-10");
        colMap.put("#f6ba9c", ".clsWindow-11");
        colMap.put("#b5ad9d", ".clsWindow-12");
        colMap.put("#371912", ".clsWindow-13");
        colMap.put("#005bab", ".clsWindow-14");
        colMap.put("#a64e1e", ".clsWindow-15");
        colMap.put("#7b8874", ".clsWindow-16");
        colMap.put("#d4858f", ".clsWindow-17");
        colMap.put("#97200b", ".clsWindow-18");
        colMap.put("#421a20", ".clsWindow-19");
        colMap.put("#009597", ".clsWindow-20");
    }

    private void run() {
        try {
            File file = FileUtil.getFileFromPath(FILE_PATH);
            List<String> lines = FileUtil.loadFile(file);
            StringBuilder sb = new StringBuilder();
            for(String line : lines) {
                sb.append(line);
            }
            String toParse = sb.toString();
            int styleStart = toParse.indexOf("<style>") + "<style>".length();
            int styleEnd = toParse.indexOf("</style>");
            String styles = toParse.substring(styleStart, styleEnd);

            Map<String, String> replaceMap = new HashMap<>();

            int clsStart = styles.indexOf(CLS);
            while (clsStart >= 0) {
                int clsDefStart = clsStart + CLS.length();
                int clsNoEnd = styles.indexOf("{", clsDefStart);
                String clsNo = styles.substring(clsDefStart, clsNoEnd);

                int fillStart = styles.indexOf(FILL, clsNoEnd) + FILL.length();
                int fillEnd = styles.indexOf(";", fillStart);
                String fill = styles.substring(fillStart, fillEnd);

                if(!colMap.containsKey(fill)) {
                    throw new Exception("Invalid fill: " + fill + " clsNo: " + clsNo);
                }
                String mappedCls = colMap.get(fill);
                replaceMap.put(CLS + clsNo, mappedCls);

                LOG.info("Have clsNo: {}, fill: {}", clsNo, fill);

                clsStart = styles.indexOf(CLS, clsDefStart);
            }

            String mappedContent = toParse;
            for(String cls : replaceMap.keySet()) {
                String mappedCls = replaceMap.get(cls);
                String c1 = cls.substring(1);
                String c2 = mappedCls.substring(1);
                LOG.info("Replace cls: {}, with: {}", c1, c2);
                mappedContent = mappedContent.replaceAll(c1 + "(?![0-9])", c2);
            }

            FileUtil.writeToFile(mappedContent, DIR_PATH + "replaced-" + FILE_NAME);
        } catch (Exception e) {
            LOG.error("Failed to load map file {}", FILE_PATH, e);
        }

    }

    public static void main(String[] args) {
        SvgColourReplacer replacer = new SvgColourReplacer();
        replacer.run();
    }


}
