#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_quickjs_android_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_quickjs_android_QuickJS_execute(JNIEnv *env, jclass clazz, jstring global_alias,
                                         jstring temp_directory) {
    // TODO: implement execute()
    return NULL;
}