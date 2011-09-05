package com.sj.freebase.utils;

public class StringUtils {

    public static String combine(String ... str) {
        StringBuilder combiner = new StringBuilder();
        
        for (String string : str) {
            combiner.append(string);
        }
        
        return combiner.toString();
    }
}
