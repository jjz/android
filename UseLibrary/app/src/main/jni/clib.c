#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <openssl/rand.h>
//#include <llib.h>

#define LOG_TAG "jni-log"
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)


JNIEXPORT jbyteArray JNICALL
Java_jjz_uselibrary_NativeUtil_getRandom(JNIEnv *env, jclass type) {
    unsigned char rand_str[128];
    RAND_seed(rand_str, 32);
    jbyteArray bytes = (*env)->NewByteArray(env, 128);
    (*env)->SetByteArrayRegion(env, bytes, 0, 128, rand_str);
    return bytes;


}

JNIEXPORT void JNICALL
Java_jjz_uselibrary_NativeUtil_callLogFromJni(JNIEnv *env, jclass type) {

    __android_log_print(ANDROID_LOG_INFO, "jni-log", "from jni log");
    LOGW("log from  define");


}

JNIEXPORT void JNICALL
Java_jjz_uselibrary_NativeUtil_callJavaStaticMethodFromJni(JNIEnv *env, jclass type) {


}

JNIEXPORT void JNICALL
Java_jjz_uselibrary_NativeUtil_callJavaMethodFromJni(JNIEnv *env, jclass type) {



}