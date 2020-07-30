package com.xenaksys.szcore.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class FileUtil {
    private static Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private static final String CHARSET = "UTF-8";

    public static List<String> loadLinesFromFile(String path) {
        try {
            URL url = ClassLoader.getSystemResource(path);
            File file = FileUtils.toFile(url);
            if (!file.exists()) {
                LOG.error("File does not exist for path: " + path);
                return null;
            }

            return loadFile(file);
        } catch (Exception e) {
            LOG.error("Failed to process file path: " + path);
        }
        return null;
    }

    public static File getFileFromClassPath(String path) {
        try {
            URL url = ClassLoader.getSystemResource(path);
            File file = FileUtils.toFile(url);
            if (!file.exists()) {
                LOG.error("File does not exist for path: " + path);
                return null;
            }

            return file;
        } catch (Exception e) {
            LOG.error("Failed to process file path: " + path);
        }
        return null;
    }

    public static File getFileFromPath(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                LOG.error("File does not exist for path: " + path);
                return null;
            }

            return file;
        } catch (Exception e) {
            LOG.error("Failed to process file path: " + path);
        }
        return null;
    }

    public static List<String> loadFile(File file) throws Exception {
        return FileUtils.readLines(file, CHARSET);
    }

    public static File loadFile(String path) {
        try {
            URL url = ClassLoader.getSystemResource(path);
            File file = FileUtils.toFile(url);
            if (!file.exists()) {
                LOG.error("File does not exist for path: " + path);
                return null;
            }

            return file;
        } catch (Exception e) {
            LOG.error("Failed to process file path: " + path);
        }
        return null;
    }

    public static void writeToFile(String text, String filePath) {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(text);
        } catch (IOException e) {
            LOG.error("Failed to write to file {}", filePath, e);
        }
    }

    public static String readYamlFromLocalResource(String theName) {
        if (!(theName.endsWith(".yaml") || theName.endsWith(".yml"))) {
            throw new RuntimeException("Invalid file type: " + theName);
        }
        InputStream input = FileUtil.class.getClassLoader().getResourceAsStream(theName);
        return readYaml(input);
    }

    public static String readYamlFromFile(File file) {
        if (!(file.getName().endsWith(".yaml") || file.getName().endsWith(".yml"))) {
            throw new RuntimeException("Invalid file type: " + file.getName());
        }

        if (!file.exists()) {
            throw new RuntimeException("Invalid file: " + file.getName());
        }


        InputStream input;
        try {
            input = new FileInputStream(file);
        } catch (Exception e) {
            throw new RuntimeException("Invalid file input stream: " + file.getName());
        }

        return readYaml(input);
    }

    public static String readYaml(InputStream input) {
        try {
            if (input == null) {
                throw new RuntimeException("Invalid input stream");
            }
            BufferedInputStream is = new BufferedInputStream(input);
            StringBuilder buf = new StringBuilder(3000);
            int i;
            try {
                while ((i = is.read()) != -1) {
                    buf.append((char) i);
                }
            } finally {
                is.close();
            }
            String resource = buf.toString();
            // convert EOLs
            String[] lines = resource.split("\\r?\\n");
            StringBuilder buffer = new StringBuilder();
            for (int j = 0; j < lines.length; j++) {
                buffer.append(lines[j]);
                buffer.append("\n");
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
