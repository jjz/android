package com.jjz;

public class StringUtil {
    public static boolean compare(String source, String other) {
        if (source == null) {
            return other == null;
        } else {
            return source.equals(other);
        }

    }
}
