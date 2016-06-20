package com.jjz;

public class JniHandle {

    public static String getStringFromStatic() {
        return "string from static method in java";
    }

    public String getStringForJava() {
        return "string from method in java";
    }
}
