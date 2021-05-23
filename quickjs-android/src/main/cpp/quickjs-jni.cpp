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
extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1createRuntime(JNIEnv *env, jclass clazz) {
    JSRuntime *runtime = JS_NewRuntime();
    return reinterpret_cast<jlong>(runtime);
}extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1createContext(JNIEnv *env, jclass clazz, jlong runtime_ptr) {
    JSRuntime *runtime = reinterpret_cast<JSRuntime *>(runtime_ptr);
    JSContext *context = JS_NewContext(runtime);
    return reinterpret_cast<jlong>(context);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1releaseRuntime(JNIEnv *env, jclass clazz, jlong runtime_ptr) {
    JSRuntime *runtime = reinterpret_cast<JSRuntime *>(runtime_ptr);
    JS_FreeRuntime(runtime);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1releaseContext(JNIEnv *env, jclass clazz, jlong context_ptr) {
    JSContext *context = reinterpret_cast<JSContext *>(context_ptr);
    JS_FreeContext(context);
}

JSValue executeScript(JNIEnv *env, jclass clazz, jlong context_ptr, jstring source_,
                      jstring file_name_) {
    JSContext *context = reinterpret_cast<JSContext *>(context_ptr);
    const char *source = env->GetStringUTFChars(source_, NULL);
    const int source_length = env->GetStringUTFLength(source_);
    const char *file_name = env->GetStringUTFChars(file_name_, NULL);
    int flags = 0;
    JSValue val = JS_Eval(context, source, (size_t) source_length, file_name, flags);
    return val;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_quickjs_android_QuickJS__1executeIntegerScript(JNIEnv *env, jclass clazz,
                                                        jlong context_ptr, jstring source,
                                                        jstring file_name) {
    JSValue val = executeScript(env, clazz, context_ptr, source, file_name);
    return JS_VALUE_GET_INT(val);
}extern "C"
JNIEXPORT jdouble JNICALL
Java_com_quickjs_android_QuickJS__1executeDoubleScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                       jstring source, jstring file_name) {
    JSValue val = executeScript(env, clazz, context_ptr, source, file_name);
    return JS_VALUE_GET_FLOAT64(val);
}extern "C"
JNIEXPORT jstring JNICALL
Java_com_quickjs_android_QuickJS__1executeStringScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                       jstring source, jstring file_name) {
    JSContext *context = reinterpret_cast<JSContext *>(context_ptr);
    JSValue val = executeScript(env, clazz, context_ptr, source, file_name);
    const char *str = JS_ToCString(context, val);
    jstring j_str = env->NewStringUTF(str);
    JS_FreeCString(context, str);
    return j_str;
}extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_android_QuickJS__1executeBooleanScript(JNIEnv *env, jclass clazz,
                                                        jlong context_ptr, jstring source,
                                                        jstring file_name) {
    JSValue val = executeScript(env, clazz, context_ptr, source, file_name);
    return JS_VALUE_GET_BOOL(val);
}extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jstring source, jstring file_name) {
    JSContext *context = reinterpret_cast<JSContext *>(context_ptr);
    JSValue val = executeScript(env, clazz, context_ptr, source, file_name);
    if (JS_IsString(val)) {

    } else if (JS_IsBool(val)) {

    } else if (JS_IsBigFloat(val)) {
        // double
    } else if (JS_IsBigInt(context, val)) {
        // long
    } else if (JS_IsNull(val)) {
        return NULL;
    } else if (JS_IsNumber(val)) {

    }
    return NULL;
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1executeVoidScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                     jstring source, jstring file_name) {
    executeScript(env, clazz, context_ptr, source, file_name);
}