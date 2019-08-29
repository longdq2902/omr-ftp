package com.cael.omr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Utils {

    public static String getValueOfArgs(String input, String key, String defaultValue) {
        if (StringUtils.isBlank(input)) {
            return defaultValue;
        }
        if (StringUtils.isBlank(key)) {
            return defaultValue;
        }
        String tmpKey = String.format("--%s", key);
        if (StringUtils.startsWith(input, tmpKey)) {
            return input.substring(tmpKey.length() + 1).trim();
        }
        return defaultValue;
    }

    public static String getValueOfArgs(String key, String defaultValue, String... args) {
        String tmpKey = String.format("--%s", key);
        for (String s : args) {
            if (StringUtils.startsWith(s, tmpKey))
                return getValueOfArgs(s, key, defaultValue);
        }
        log.warn("Not found Args {} --> Return value: {}", key, defaultValue);
        return defaultValue;
    }
}
