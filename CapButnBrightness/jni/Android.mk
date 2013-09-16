LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := CLib
LOCAL_SRC_FILES := CLib.c

include $(BUILD_SHARED_LIBRARY)
