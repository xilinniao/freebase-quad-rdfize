package com.sj.util;

import com.google.gson.Gson;

public class JsonUtils {

    public static String toJson(Gson gson, Object object) {
        return gson.toJson(object);
    }
}
