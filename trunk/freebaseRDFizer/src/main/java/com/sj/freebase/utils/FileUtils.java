package com.sj.freebase.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class FileUtils {
    private static Logger logger = Logger.getLogger(FileUtils.class);

    // TODO : Handle comments
    public static String readRdfizerConfig(String fileName) {

        String json = "";

        try {
            FileInputStream in = new FileInputStream(new File(fileName));
            BufferedReader br =
                new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                json = StringUtils.combine(json, line);
            }
        } catch (Exception e) {
            logger.debug("Failed to read file : " + fileName);
        }

        if (json == null) {
            json = "";
        }

        return json;
    }
}
