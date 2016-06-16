#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_jjz_NativeUtil_firstNative(JNIEnv *env, jclass type) {
    return (*env)->NewStringUTF(env, "is first native");
}