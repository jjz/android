package com.jjz;

public class NativeUtil {
    static {
        System.loadLibrary("experiment");
    }

    public static native String firstNative();
}
