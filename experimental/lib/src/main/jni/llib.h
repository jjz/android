#include <android/log.h>

#define  LOG_TAG "jni-lib"
#define  LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

extern  void  callMethodFromJniLib();