LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE :=hello
LOCAL_CFlAGS += -mllvm -sub -mllvm -bcf -mllvm -fla

LOCAL_SRC_FILES =: com_example_jjz_jni_HelloJni.c
include $(BUILD_SHARED_LIBRARY)