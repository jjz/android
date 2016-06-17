package com.jjz;

public class NativeUtil {
    static {
        System.loadLibrary("experiment");
    }

    public static native String firstNative();
    public static  native byte[] getRandom();
}
