#include <jni.h>
#include <string>
#include <quickjs/quickjs.h>


extern "C"
JNIEXPORT jint JNICALL
Java_com_quickjs_android_QuickJS_executeIntegerScript(JNIEnv *env, jclass clazz, jstring jCode,
                                                      jstring jFileName) {
    JSRuntime *jsRuntime = JS_NewRuntime();
    JSContext *context = JS_NewContext(jsRuntime);
    const char *code = env->GetStringUTFChars(jCode, NULL);
    const int code_length = env->GetStringUTFLength(jCode);
    const char *file_name = env->GetStringUTFChars(jFileName, NULL);
    int flags = 0;
    JSValue val = JS_Eval(context, code, (size_t) code_length, file_name, flags);
    int result = JS_VALUE_GET_INT(val);
    return result;
}