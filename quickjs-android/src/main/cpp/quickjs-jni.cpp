#include <jni.h>
#include <string>
#include <quickjs/quickjs.h>


extern "C"
JNIEXPORT jint JNICALL
Java_com_quickjs_android_QuickJS_executeIntegerScript(JNIEnv *env, jclass clazz, jstring jCode,
                                                      jstring jFileName) {
    JSRuntime *jsRuntime = JS_NewRuntime();
    JSContext *ctx = JS_NewContext(jsRuntime);
    const char *code = env->GetStringUTFChars(jCode, NULL);
    const int code_length = env->GetStringUTFLength(jCode);
    const char *file_name = env->GetStringUTFChars(jFileName, NULL);
    int flags = 0;
    JSValue val = JS_Eval(ctx, code, (size_t) code_length, file_name, flags);
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
    JSContext *ctx = JS_NewContext(runtime);
    return reinterpret_cast<jlong>(ctx);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1releaseRuntime(JNIEnv *env, jclass clazz, jlong runtime_ptr) {
    JSRuntime *runtime = reinterpret_cast<JSRuntime *>(runtime_ptr);
    JS_FreeRuntime(runtime);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1releaseContext(JNIEnv *env, jclass clazz, jlong context_ptr) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JS_FreeContext(ctx);
}

JSValue executeScript(JNIEnv *env, jclass clazz, jlong context_ptr, jstring source_,
                      jstring file_name_) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    const char *source = env->GetStringUTFChars(source_, NULL);
    const int source_length = env->GetStringUTFLength(source_);
    const char *file_name = env->GetStringUTFChars(file_name_, NULL);
    int flags = 0;
    JSValue val = JS_Eval(ctx, source, (size_t) source_length, file_name, flags);
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
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue val = executeScript(env, clazz, context_ptr, source, file_name);
    const char *str = JS_ToCString(ctx, val);
    jstring j_str = env->NewStringUTF(str);
    JS_FreeCString(ctx, str);
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
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue val = executeScript(env, clazz, context_ptr, source, file_name);
    if (JS_IsString(val)) {

    } else if (JS_IsBool(val)) {

    } else if (JS_IsBigFloat(val)) {
        // double
    } else if (JS_IsBigInt(ctx, val)) {
        // long
    } else if (JS_IsNull(val)) {
        return NULL;
    } else if (JS_IsNumber(val)) {

    }
    return NULL;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1executeVoidScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                     jstring source, jstring file_name) {
    executeScript(env, clazz, context_ptr, source, file_name);
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1getGlobalObject(JNIEnv *env, jclass clazz, jlong context_ptr) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue global_obj = JS_GetGlobalObject(ctx);
    return global_obj;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1add__JJLjava_lang_String_2I(JNIEnv *env, jclass clazz,
                                                               jlong context_ptr,
                                                               jlong object_handle, jstring key_,
                                                               jint value_) {
    const char *key = env->GetStringUTFChars(key_, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_NewInt32(ctx, value_);
    JS_SetPropertyStr(ctx, this_obj, key, jsValue);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1add__JJLjava_lang_String_2Ljava_lang_String_2(JNIEnv *env,
                                                                                 jclass clazz,
                                                                                 jlong context_ptr,
                                                                                 jlong object_handle,
                                                                                 jstring key_,
                                                                                 jstring value_) {
    const char *key = env->GetStringUTFChars(key_, NULL);
    const char *value = env->GetStringUTFChars(value_, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_NewString(ctx, value);
    JS_SetPropertyStr(ctx, this_obj, key, jsValue);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1add__JJLjava_lang_String_2D(JNIEnv *env, jclass clazz,
                                                               jlong context_ptr,
                                                               jlong object_handle, jstring key_,
                                                               jdouble value_) {
    const char *key = env->GetStringUTFChars(key_, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_NewFloat64(ctx, value_);
    JS_SetPropertyStr(ctx, this_obj, key, jsValue);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1add__JJLjava_lang_String_2Z(JNIEnv *env, jclass clazz,
                                                               jlong context_ptr,
                                                               jlong object_handle, jstring key_,
                                                               jboolean value_) {
    const char *key = env->GetStringUTFChars(key_, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_NewBool(ctx, value_);
    JS_SetPropertyStr(ctx, this_obj, key, jsValue);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1initNewJSObject(JNIEnv *env, jclass clazz, jlong context_ptr) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue jsValue = JS_NewObject(ctx);
    return jsValue;
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1addObject(JNIEnv *env, jclass clazz, jlong context_ptr,
                                             jlong object_handle, jstring key_, jlong value_ptr) {
    const char *key = env->GetStringUTFChars(key_, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = value_ptr;
    JS_SetPropertyStr(ctx, this_obj, key, jsValue);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1release(JNIEnv *env, jclass clazz, jlong context_ptr,
                                           jlong object_handle) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JS_FreeValue(ctx, this_obj);
}extern "C"
JNIEXPORT jint JNICALL
Java_com_quickjs_android_QuickJS__1getInteger(JNIEnv *env, jclass clazz, jlong context_ptr,
                                              jlong object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyStr(ctx, this_obj, key_);
    return JS_VALUE_GET_INT(jsValue);
}extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_android_QuickJS__1getBoolean(JNIEnv *env, jclass clazz, jlong context_ptr,
                                              jlong object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyStr(ctx, this_obj, key_);
    return JS_VALUE_GET_BOOL(jsValue);

}extern "C"
JNIEXPORT jdouble JNICALL
Java_com_quickjs_android_QuickJS__1getDouble(JNIEnv *env, jclass clazz, jlong context_ptr,
                                             jlong object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyStr(ctx, this_obj, key_);
    return JS_VALUE_GET_FLOAT64(jsValue);
}extern "C"
JNIEXPORT jstring JNICALL
Java_com_quickjs_android_QuickJS__1getString(JNIEnv *env, jclass clazz, jlong context_ptr,
                                             jlong object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyStr(ctx, this_obj, key_);
    const char *str = JS_ToCString(ctx, jsValue);
    jstring j_str = env->NewStringUTF(str);
    return j_str;
}