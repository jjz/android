#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <openssl/rand.h>
#include <llib.h>


#define LOG_TAG "jni-log"
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)


JNIEXPORT jstring JNICALL
Java_com_jjz_NativeUtil_firstNative(JNIEnv *env, jclass type) {
    char chars[] = "i am test";
    int p=0;
    //p=1/0;
    return (*env)->NewStringUTF(env, chars);
}

//
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
   // callMethodFromJniLib();
    __android_log_print(ANDROID_LOG_INFO, "jni-log", "from jni log");
    LOGW("log from  define");

}
//
JNIEXPORT void JNICALL
Java_com_jjz_NativeUtil_callJavaStaticMethodFromJni(JNIEnv *env, jclass type) {

    jclass jniHandle = (*env)->FindClass(env, "com/jjz/JniHandle");
    if (NULL == jniHandle) {
        LOGW("can't find JniHandle");
        return;
    }
    jmethodID getStringFromStatic = (*env)->GetStaticMethodID(env, jniHandle, "getStringFromStatic",
                                                              "()Ljava/lang/String;");
    if (NULL == getStringFromStatic) {
        (*env)->DeleteLocalRef(env, jniHandle);
        LOGW("can't find method getStringFromStatic from JniHandle ");
        return;
    }
    jstring result = (*env)->CallStaticObjectMethod(env, jniHandle, getStringFromStatic);
    const char *resultChar = (*env)->GetStringUTFChars(env, result, NULL);
    (*env)->DeleteLocalRef(env, jniHandle);
    (*env)->DeleteLocalRef(env, result);
    LOGW(resultChar);


}

JNIEXPORT void JNICALL
Java_com_jjz_NativeUtil_callJavaMethodFromJni(JNIEnv *env, jclass type) {

    jclass jniHandle = (*env)->FindClass(env, "com/jjz/JniHandle");
    if (NULL == jniHandle) {
        LOGW("can't find jniHandle");
        return;
    }
    jmethodID constructor = (*env)->GetMethodID(env, jniHandle, "<init>", "()V");
    if (NULL == constructor) {
        LOGW("can't constructor JniHandle");
        return;
    }
    jobject jniHandleObject = (*env)->NewObject(env, jniHandle, constructor);
    if (NULL == jniHandleObject) {
        LOGW("can't new JniHandle");
        return;
    }
    jmethodID getStringForJava = (*env)->GetMethodID(env, jniHandle, "getStringForJava",
                                                     "()Ljava/lang/String;");
    if (NULL == getStringForJava) {
        LOGW("can't find method of getStringForJava");
        (*env)->DeleteLocalRef(env, jniHandle);
        (*env)->DeleteLocalRef(env, jniHandleObject);
        return;
    }
    jstring result = (*env)->CallObjectMethod(env, jniHandleObject, getStringForJava);
    const char *resultChar = (*env)->GetStringUTFChars(env, result, NULL);
    (*env)->DeleteLocalRef(env, jniHandle);
    (*env)->DeleteLocalRef(env, jniHandleObject);
    (*env)->DeleteLocalRef(env, result);
    LOGW(resultChar);


}