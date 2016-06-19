#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <openssl/rand.h>

#define LOG_TAG "jni-log"
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)



JNIEXPORT jstring JNICALL
Java_com_jjz_NativeUtil_firstNative(JNIEnv *env, jclass type) {
    char chars[] = "i am test";
    int b = 1;

    return (*env)->NewStringUTF(env, chars);
}


JNIEXPORT jbyteArray JNICALL
Java_com_jjz_NativeUtil_getRandom(JNIEnv *env, jclass type) {
    unsigned char rand_str[128];
    RAND_seed(rand_str, 32);
    jbyteArray bytes = (*env)->NewByteArray(env, 128);
    (*env)->SetByteArrayRegion(env, bytes, 0, 128, rand_str);
    return bytes;

}

JNIEXPORT void JNICALL
Java_com_jjz_NativeUtil_callLogFromJni(JNIEnv *env, jclass type) {

    __android_log_print(ANDROID_LOG_INFO,"jni-log","from jni log");
    LOGW("log from  define");

}