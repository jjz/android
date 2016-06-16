#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_jjz_NativeUtil_firstNative(JNIEnv *env, jclass type) {
    char chars[] = "i am test";
    int b=1;
    return (*env)->NewStringUTF(env, chars);
}