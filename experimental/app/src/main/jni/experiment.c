#include <jni.h>

//#include "openssl/rand.h"

JNIEXPORT jstring JNICALL
Java_com_jjz_NativeUtil_firstNative(JNIEnv *env, jclass type) {
    char chars[] = "i am test";
    int b=1;
    unsigned char rand_str[128];
  //  RAND_seed(rand_str,32);
    return (*env)->NewStringUTF(env, rand_str);
}