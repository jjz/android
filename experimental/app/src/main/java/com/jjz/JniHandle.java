package com.jjz;

public class JniHandle {

    public static String getStringFromStatic() {
        return "string from static method";
    }

    public String getStringForJava() {
        return "string from method";
    }
}
