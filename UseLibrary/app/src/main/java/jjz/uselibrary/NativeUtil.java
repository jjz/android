package jjz.uselibrary;


public class NativeUtil {

    static {
        System.loadLibrary("clib");
    }


    public static native byte[] getRandom();

    public static native void callLogFromJni();

    public static native void callJavaStaticMethodFromJni();

    public static native void callJavaMethodFromJni();
}
