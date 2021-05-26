#include <jni.h>
#include <string>
#include <quickjs/quickjs.h>

const int TYPE_NULL = 0;
const int TYPE_UNKNOWN = 0;
const int TYPE_INTEGER = 1;
const int TYPE_INT_32_ARRAY = 1;
const int TYPE_DOUBLE = 2;
const int TYPE_FLOAT_64_ARRAY = 2;
const int TYPE_BOOLEAN = 3;
const int TYPE_STRING = 4;
const int TYPE_JS_ARRAY = 5;
const int TYPE_JS_OBJECT = 6;
const int TYPE_JS_FUNCTION = 7;
const int TYPE_JS_TYPED_ARRAY = 8;
const int TYPE_BYTE = 9;
const int TYPE_INT_8_ARRAY = 9;
const int TYPE_JS_ARRAY_BUFFER = 10;
const int TYPE_UNSIGNED_INT_8_ARRAY = 11;
const int TYPE_UNSIGNED_INT_8_CLAMPED_ARRAY = 12;
const int TYPE_INT_16_ARRAY = 13;
const int TYPE_UNSIGNED_INT_16_ARRAY = 14;
const int TYPE_UNSIGNED_INT_32_ARRAY = 15;
const int TYPE_FLOAT_32_ARRAY = 16;
const int TYPE_UNDEFINED = 99;

jclass integerCls = nullptr;
jclass longCls = nullptr;
jclass doubleCls = nullptr;
jclass booleanCls = nullptr;

jmethodID integerInitMethodID = nullptr;
jmethodID longInitMethodID = nullptr;
jmethodID doubleInitMethodID = nullptr;
jmethodID booleanInitMethodID = nullptr;

jclass quickJSCls = nullptr;
jmethodID callJavaVoidCallbackMethodID = nullptr;

jobject To_JObject(JNIEnv *env, jlong context_ptr, int expected_type, JSValue result) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    switch (expected_type) {
        case TYPE_NULL:
            return nullptr;
        case TYPE_INTEGER:
            return env->NewObject(integerCls, integerInitMethodID, JS_VALUE_GET_INT(result));
        case TYPE_DOUBLE:
            double pres;
            JS_ToFloat64(ctx, &pres, result);
            return env->NewObject(doubleCls, doubleInitMethodID, pres);
        case TYPE_BOOLEAN:
            return env->NewObject(booleanCls, booleanInitMethodID, JS_VALUE_GET_BOOL(result));
        case TYPE_STRING:
            return env->NewStringUTF(JS_ToCString(ctx, result));
        case TYPE_JS_ARRAY:
            return env->NewObject(integerCls, integerInitMethodID, (long) result);
        case TYPE_JS_OBJECT:
            return env->NewObject(integerCls, integerInitMethodID, (long) result);
    }
    if (JS_IsArray(ctx, result)) {
        // TODO
        return env->NewObject(integerCls, integerInitMethodID, (long) result);
    } else if (JS_IsObject(result)) {
        // TODO
        return env->NewObject(integerCls, integerInitMethodID, (long) result);
    } else if (JS_IsString(result)) {
        return env->NewStringUTF(JS_ToCString(ctx, result));
    } else if (JS_IsBool(result)) {
        return env->NewObject(booleanCls, booleanInitMethodID, JS_VALUE_GET_BOOL(result));
    } else if (JS_IsBigFloat(result)) {
        double pres;
        JS_ToFloat64(ctx, &pres, result);
        return env->NewObject(doubleCls, doubleInitMethodID, pres);
    } else if (JS_IsBigInt(ctx, result)) {
        // long
    } else if (JS_IsNull(result)) {
        return nullptr;
    } else if (JS_IsNumber(result)) {

    }
    return nullptr;
}

JavaVM *jvm;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
    jvm = vm;
    JNIEnv *env;
    jint onLoad_err = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return onLoad_err;
    }
    if (env == nullptr) {
        return onLoad_err;
    }

    integerCls = (jclass) env->NewGlobalRef((env)->FindClass("java/lang/Integer"));
    longCls = (jclass) env->NewGlobalRef((env)->FindClass("java/lang/Long"));
    doubleCls = (jclass) env->NewGlobalRef((env)->FindClass("java/lang/Double"));
    booleanCls = (jclass) env->NewGlobalRef((env)->FindClass("java/lang/Boolean"));
    quickJSCls = (jclass) env->NewGlobalRef((env)->FindClass("com/quickjs/android/QuickJS"));

    integerInitMethodID = env->GetMethodID(integerCls, "<init>", "(I)V");
    longInitMethodID = env->GetMethodID(longCls, "<init>", "(J)V");
    doubleInitMethodID = env->GetMethodID(doubleCls, "<init>", "(D)V");
    booleanInitMethodID = env->GetMethodID(booleanCls, "<init>", "(Z)V");
    callJavaVoidCallbackMethodID = env->GetStaticMethodID(quickJSCls, "callJavaVoidCallback",
                                                          "(JJJJ)V");
    return JNI_VERSION_1_6;
}

int getArrayLength(JSContext *ctx, JSValue this_obj) {
    JSValue lenValue = JS_GetPropertyStr(ctx, this_obj, "length");
    return JS_VALUE_GET_INT(lenValue);
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1createRuntime(JNIEnv *env, jclass clazz) {
    JSRuntime *runtime = JS_NewRuntime();
    return reinterpret_cast<jlong>(runtime);
}
extern "C"
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
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jint expected_type,
                                                 jstring source, jstring file_name) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    const char *source_ = env->GetStringUTFChars(source, NULL);
    const int source_length = env->GetStringUTFLength(source);
    const char *file_name_ = env->GetStringUTFChars(file_name, NULL);
    JSValue val = JS_Eval(ctx, source_, (size_t) source_length, file_name_, JS_EVAL_TYPE_GLOBAL);
    jobject result = To_JObject(env, context_ptr, expected_type, val);
    // TODO
    JS_FreeValue(ctx, val);
    return result;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1getGlobalObject(JNIEnv *env, jclass clazz, jlong context_ptr) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue global_obj = JS_GetGlobalObject(ctx);
    return (jlong) global_obj;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1set__JJLjava_lang_String_2I(JNIEnv *env, jclass clazz,
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
Java_com_quickjs_android_QuickJS__1set__JJLjava_lang_String_2Ljava_lang_String_2(JNIEnv *env,
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
Java_com_quickjs_android_QuickJS__1set__JJLjava_lang_String_2D(JNIEnv *env, jclass clazz,
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
Java_com_quickjs_android_QuickJS__1set__JJLjava_lang_String_2Z(JNIEnv *env, jclass clazz,
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
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1initNewJSArray(JNIEnv *env, jclass clazz, jlong context_ptr) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue jsValue = JS_NewArray(ctx);
    return jsValue;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1setObject(JNIEnv *env, jclass clazz, jlong context_ptr,
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

}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1getObject(JNIEnv *env, jclass clazz, jlong context_ptr,
                                             jlong object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyStr(ctx, this_obj, key_);
    return jsValue;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_quickjs_android_QuickJS__1getDouble(JNIEnv *env, jclass clazz, jlong context_ptr,
                                             jlong object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyStr(ctx, this_obj, key_);
    double pres;
    JS_ToFloat64(ctx, &pres, jsValue);
    return pres;
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

extern "C"
JNIEXPORT jstring JNICALL
Java_com_quickjs_android_QuickJS__1arrayGetString(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                  jlong object_handle, jint index) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyUint32(ctx, this_obj, index);
    const char *str = JS_ToCString(ctx, jsValue);
    jstring j_str = env->NewStringUTF(str);
    return j_str;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_quickjs_android_QuickJS__1arrayGetDouble(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                  jlong object_handle, jint index) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyUint32(ctx, this_obj, index);
    double pres;
    JS_ToFloat64(ctx, &pres, jsValue);
    return pres;
}extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_android_QuickJS__1arrayGetBoolean(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                   jlong object_handle, jint index) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyUint32(ctx, this_obj, index);
    return JS_VALUE_GET_BOOL(jsValue);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_quickjs_android_QuickJS__1arrayGetInteger(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                   jlong object_handle, jint index) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyUint32(ctx, this_obj, index);
    return JS_VALUE_GET_INT(jsValue);
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1arrayGetObject(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                  jlong object_handle, jint index) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyUint32(ctx, this_obj, index);
    if (JS_IsNull(jsValue)) return 0;
    if (JS_IsObject(jsValue)) return jsValue;
    return 0;
}extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1arrayGetArray(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jlong object_handle, jint index) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue jsValue = JS_GetPropertyUint32(ctx, this_obj, index);
    if (JS_IsNull(jsValue)) return 0;
    if (JS_IsArray(ctx, jsValue)) return jsValue;
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1arrayAdd__JJI(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jlong object_handle, jint value) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue value_ = JS_NewInt32(ctx, value);
    JS_SetPropertyUint32(ctx, this_obj, getArrayLength(ctx, this_obj), value_);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1arrayAdd__JJD(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jlong object_handle, jdouble value) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue value_ = JS_NewFloat64(ctx, value);
    JS_SetPropertyUint32(ctx, this_obj, getArrayLength(ctx, this_obj), value_);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1arrayAdd__JJZ(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jlong object_handle, jboolean value) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue value_ = JS_NewBool(ctx, value);
    JS_SetPropertyUint32(ctx, this_obj, getArrayLength(ctx, this_obj), value_);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1arrayAddObject(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                  jlong object_handle, jlong value) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue value_ = value;
    JS_SetPropertyUint32(ctx, this_obj, getArrayLength(ctx, this_obj), value_);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1arrayAdd__JJLjava_lang_String_2(JNIEnv *env, jclass clazz,
                                                                   jlong context_ptr,
                                                                   jlong object_handle,
                                                                   jstring value) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    const char *value_str = env->GetStringUTFChars(value, NULL);
    JSValue value_ = JS_NewString(ctx, value_str);
    JS_SetPropertyUint32(ctx, this_obj, getArrayLength(ctx, this_obj), value_);
}extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_android_QuickJS__1contains(JNIEnv *env, jclass clazz, jlong context_ptr,
                                            jlong object_handle, jstring key) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    const char *key_ = env->GetStringUTFChars(key, NULL);
    JSAtom atom = JS_NewAtom(ctx, key_);
    int result = JS_HasProperty(ctx, this_obj, atom);
    JS_FreeAtom(ctx, atom);
    return result;
}extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_quickjs_android_QuickJS__1getKeys(JNIEnv *env, jclass clazz, jlong context_ptr,
                                           jlong object_handle) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSPropertyEnum *tab;
    uint32_t len;
    JS_GetOwnPropertyNames(ctx, &tab, &len, this_obj, JS_GPN_STRING_MASK | JS_GPN_ENUM_ONLY);
    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray stringArray = env->NewObjectArray(len, strClass, NULL);
    for (int i = 0; i < len; ++i) {
        jstring key = env->NewStringUTF(JS_AtomToCString(ctx, tab[i].atom));
        env->SetObjectArrayElement(stringArray, i, key);
    }
    return stringArray;
}

JSValue executeJSFunction(JNIEnv *env,
                          jlong context_ptr, jlong object_handle,
                          jstring name, jlong parameters_handle) {
    const char *name_ = env->GetStringUTFChars(name, NULL);
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue func_obj = JS_GetPropertyStr(ctx, this_obj, name_);
    JSValue *argv = NULL;
    if (parameters_handle != 0) {
        JSValue jsValue = parameters_handle;
        argv = &jsValue;
    }
    return JS_Call(ctx, func_obj, this_obj, 1, argv);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeFunction(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                   jint expected_type, jlong object_handle,
                                                   jstring name, jlong parameters_handle) {
    JSValue result = executeJSFunction(env, context_ptr, object_handle, name, parameters_handle);
    return To_JObject(env, context_ptr, expected_type, result);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeFunction2(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                    jint expected_type, jlong object_handle,
                                                    jlong functionHandle, jlong parameters_handle) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = object_handle;
    JSValue func_obj = functionHandle;
    JSValue *argv = NULL;
    if (parameters_handle != 0) {
        JSValue jsValue = parameters_handle;
        argv = &jsValue;
    }
    JSValue result = JS_Call(ctx, func_obj, this_obj, 1, argv);
    return To_JObject(env, context_ptr, expected_type, result);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeJSFunction(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                     jlong object_handle, jstring name,
                                                     jobjectArray parameters) {
    // TODO: implement _executeJSFunction()
}

JSValue
callJavaCallback(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv, int magic,
                 JSValue *func_data) {
    return JS_NewInt32(ctx, 1228);
}

typedef struct {
    long javaMethodId;
} JavaMethod;

JSValue
callJavaVoidCallback(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv, int magic,
                     JSValue *func_data) {
    JNIEnv *env;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    JSValue func = JS_GetPropertyUint32(ctx, *func_data, 0);
    JSValue args = JS_NewArray(ctx);
    for (int i = 0; i < argc; ++i) {
        JSValue it = argv[i];
        JS_SetPropertyUint32(ctx, args, i, it);
    }
    jlong contextPtr = reinterpret_cast<long>(ctx);
    jlong objectHandle = this_val;
    jlong functionHandle = func;
    jlong argsHandle = args;
    env->CallStaticVoidMethod(quickJSCls, callJavaVoidCallbackMethodID,
                              contextPtr,
                              objectHandle,
                              functionHandle,
                              argsHandle
    );
    return 0;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_android_QuickJS__1initNewJSFunction(JNIEnv *env,
                                                     jclass clazz,
                                                     jlong context_ptr,
                                                     jboolean void_method) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSCFunctionData *functionData = void_method ? callJavaVoidCallback : callJavaCallback;
    JSValue func_data = JS_NewArray(ctx);
    JSValue func = JS_NewCFunctionData(ctx, functionData, 1, 0, 1, &func_data);
    JS_SetPropertyUint32(ctx, func_data, 0, func);
    return (long) func;
}