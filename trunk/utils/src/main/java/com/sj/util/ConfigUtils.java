package com.sj.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConfigUtils {

    private static Logger logger = Logger.getLogger("ConfigUtils");
    private static final String CONFIG_PATH = "/freebase/config/Config.json";


    private static Set<String> getDataToSkip(String key) {
        Set<String> configDataToSkip = Collections.emptySet();

        try {
            String configJson = FileUtils.readRdfizerConfig(CONFIG_PATH);
            JSONParser parser = new JSONParser();
            JSONObject configObj = (JSONObject) parser.parse(configJson);

            if (configObj.containsKey(key)) {
                JSONArray domainArray = (JSONArray) configObj.get(key);

                ListIterator<String> it = domainArray.listIterator();

                if (it != null) {
                    while (it.hasNext()) {
                        String configData = it.next();
                        if (configDataToSkip.isEmpty()) {
                            configDataToSkip = new HashSet<String>();
                        }

                        configDataToSkip.add(configData);
                    }
                }

            }
        } catch (ParseException e) {
            logger.error("Failed to parse the config file : " + CONFIG_PATH +
                ", key : " + key);
        }

        if (configDataToSkip == null) {
            configDataToSkip = Collections.emptySet();
        }

        return configDataToSkip;
    }


    public static Set<String> getDomainsToSkip() {
        return getDataToSkip("domains_to_skip");
    }


    public static Set<String> getTypesToSkip() {
        return getDataToSkip("types_to_skip");
    }


    public static Set<String> getPropertiesToSkip() {
        return getDataToSkip("properties_to_skip");

    }


    public static Object get(String key) throws ParseException {
        Object value = null;
        String configJson = FileUtils.readRdfizerConfig(CONFIG_PATH);
        JSONParser parser = new JSONParser();
        JSONObject configObj = (JSONObject) parser.parse(configJson);

        value = configObj.get(key);
        return value;
    }


    public static int getIntVariable(String key) throws ParseException {
        return (Integer) get(key);
    }


    public static String getStringVariable(String key) throws ParseException {
        return (String) get(key);
    }


    public static void main(String [] args) {
        System.out.println(getDataToSkip("domains_to_skip"));
    }
}
