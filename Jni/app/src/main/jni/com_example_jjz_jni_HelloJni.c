#include "com_example_jjz_jni_HelloJni.h"


JNIEXPORT void JNICALL Java_com_example_jjz_jni_HelloJni_sayHello(JNIEnv * evn, jobject object){
    printf("hello world");
}