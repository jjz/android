#include "com_example_jjz_jni_HelloJni.h"


JNIEXPORT jstring JNICALL Java_com_example_jjz_jni_HelloJni_sayHello(JNIEnv *env, jobject object) {
    return (*env)->NewStringUTF(env, "Hello Jni");
}