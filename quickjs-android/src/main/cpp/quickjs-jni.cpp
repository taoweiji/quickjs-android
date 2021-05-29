#include <jni.h>
#include <string>
#include <quickjs/quickjs.h>
#include <vector>

#if INTPTR_MAX >= INT64_MAX
#define JS_PTR64
#define JS_PTR64_DEF(a) a
#else
#define JS_PTR64_DEF(a)
#endif

#ifndef JS_PTR64
#define JS_NAN_BOXING
#endif

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
jclass stringCls = nullptr;

jmethodID integerInitMethodID = nullptr;
jmethodID longInitMethodID = nullptr;
jmethodID doubleInitMethodID = nullptr;
jmethodID booleanInitMethodID = nullptr;

jmethodID intValueMethodID = nullptr;
jmethodID longValueMethodID = nullptr;
jmethodID doubleValueMethodID = nullptr;
jmethodID booleanValueMethodID = nullptr;


jclass quickJSCls = nullptr;
jmethodID callJavaVoidCallbackMethodID = nullptr;
jmethodID callJavaCallbackMethodID = nullptr;
jmethodID createJSValueMethodID = nullptr;

jclass jsValueCls = nullptr;
jfieldID js_value_tag_id;
jfieldID js_value_u_int32_id;
jfieldID js_value_u_float64_id;
jfieldID js_value_u_ptr_id;


JSValue TO_JS_VALUE(JNIEnv *env, jobject object_handle) {
    jlong tag = env->GetLongField(object_handle, js_value_tag_id);
#if defined(JS_NAN_BOXING)
    return tag;
#else
    JSValue value;
    value.tag = tag;
    value.u.int32 = env->GetIntField(object_handle, js_value_u_int32_id);
    value.u.float64 = env->GetDoubleField(object_handle, js_value_u_float64_id);
    value.u.ptr = (void *) env->GetLongField(object_handle, js_value_u_ptr_id);
    return value;
#endif
}

jobject TO_JAVA_OBJECT(JNIEnv *env, JSContext *ctx, JSValue value) {
    int type = TYPE_UNKNOWN;
    if (JS_IsUndefined(value)) {
        type = TYPE_UNDEFINED;
    } else if (JS_IsArray(ctx, value)) {
        type = TYPE_JS_ARRAY;
    } else if (JS_IsFunction(ctx, value)) {
        type = TYPE_JS_FUNCTION;
    } else if (JS_IsObject(value)) {
        type = TYPE_JS_OBJECT;
    }
#if defined(JS_NAN_BOXING)
    return env->CallStaticObjectMethod(quickJSCls,
                                       createJSValueMethodID,
                                       (jlong) ctx,
                                       type,
                                       (jlong) value,
                                       0,
                                       0.0,
                                       (jlong) 0);
#else
    return env->CallStaticObjectMethod(quickJSCls,
                                       createJSValueMethodID,
                                       (jlong) ctx,
                                       type,
                                       value.tag,
                                       value.u.int32,
                                       (jdouble) value.u.float64,
                                       (jlong) value.u.ptr);
#endif
}


int GetObjectType(JSContext *ctx, JSValue result) {
    if (JS_IsArray(ctx, result)) {
        return TYPE_JS_ARRAY;
    } else if (JS_IsFunction(ctx, result)) {
        return TYPE_JS_FUNCTION;
    } else if (JS_IsObject(result)) {
        return TYPE_JS_OBJECT;
    } else if (JS_IsString(result)) {
        return TYPE_STRING;
    } else if (JS_IsBigFloat(result)) {
        return TYPE_DOUBLE;
    } else if (JS_IsBool(result)) {
        return TYPE_BOOLEAN;
    } else if (JS_IsBigInt(ctx, result)) {
        return TYPE_INTEGER;
    } else if (JS_IsNull(result)) {
        return TYPE_NULL;
    } else if (JS_IsUndefined(result)) {
        return TYPE_UNDEFINED;
    } else if (JS_IsNumber(result)) {
        int tag = JS_VALUE_GET_TAG(result);
        if (tag == JS_TAG_INT) {
            return TYPE_INTEGER;
        }
        if (JS_TAG_IS_FLOAT64(tag)) {
            return TYPE_DOUBLE;
        }
    }
    return TYPE_UNKNOWN;
}

jobject To_JObject(JNIEnv *env, jlong context_ptr, int expected_type, JSValue result) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    if (expected_type == TYPE_UNKNOWN) {
        expected_type = GetObjectType(ctx, result);
    }
    if (JS_IsUndefined(result)) {
        expected_type = TYPE_UNDEFINED;
    } else if (JS_IsNull(result)) {
        expected_type = TYPE_NULL;
    }
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
        case TYPE_JS_OBJECT:
        case TYPE_JS_FUNCTION:
        case TYPE_UNDEFINED:
            return TO_JAVA_OBJECT(env, ctx, result);
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
    stringCls = (jclass) env->NewGlobalRef((env)->FindClass("java/lang/String"));
    quickJSCls = (jclass) env->NewGlobalRef((env)->FindClass("com/quickjs/android/QuickJS"));

    integerInitMethodID = env->GetMethodID(integerCls, "<init>", "(I)V");
    longInitMethodID = env->GetMethodID(longCls, "<init>", "(J)V");
    doubleInitMethodID = env->GetMethodID(doubleCls, "<init>", "(D)V");
    booleanInitMethodID = env->GetMethodID(booleanCls, "<init>", "(Z)V");

    callJavaVoidCallbackMethodID = env->GetStaticMethodID(quickJSCls, "callJavaVoidCallback",
                                                          "(Lcom/quickjs/android/JSValue;Lcom/quickjs/android/JSValue;Lcom/quickjs/android/JSArray;)V");

    callJavaCallbackMethodID = env->GetStaticMethodID(quickJSCls, "callJavaCallback",
                                                      "(Lcom/quickjs/android/JSValue;Lcom/quickjs/android/JSValue;Lcom/quickjs/android/JSArray;)Ljava/lang/Object;");

    createJSValueMethodID = env->GetStaticMethodID(quickJSCls, "createJSValue",
                                                   "(JIJIDJ)Lcom/quickjs/android/JSValue;");

    intValueMethodID = env->GetMethodID(integerCls, "intValue", "()I");
    longValueMethodID = env->GetMethodID(longCls, "longValue", "()J");
    doubleValueMethodID = env->GetMethodID(doubleCls, "doubleValue", "()D");
    booleanValueMethodID = env->GetMethodID(booleanCls, "booleanValue", "()Z");


    jsValueCls = (jclass) env->NewGlobalRef((env)->FindClass("com/quickjs/android/JSValue"));
    js_value_tag_id = env->GetFieldID(jsValueCls, "tag", "J");
    js_value_u_int32_id = env->GetFieldID(jsValueCls, "u_int32", "I");
    js_value_u_float64_id = env->GetFieldID(jsValueCls, "u_float64", "D");
    js_value_u_ptr_id = env->GetFieldID(jsValueCls, "u_ptr", "J");
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
    auto *runtime = reinterpret_cast<JSRuntime *>(runtime_ptr);
    auto *ctx = JS_NewContext(runtime);
    return reinterpret_cast<jlong>(ctx);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1releaseRuntime(JNIEnv *env, jclass clazz, jlong runtime_ptr) {
    auto *runtime = reinterpret_cast<JSRuntime *>(runtime_ptr);
    JS_FreeRuntime(runtime);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1releaseContext(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JS_FreeContext(ctx);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jint expected_type,
                                                 jstring source, jstring file_name) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    const char *source_ = env->GetStringUTFChars(source, nullptr);
    const int source_length = env->GetStringUTFLength(source);
    const char *file_name_ = env->GetStringUTFChars(file_name, nullptr);
    JSValue val = JS_Eval(ctx, source_, (size_t) source_length, file_name_, JS_EVAL_TYPE_GLOBAL);
    jobject result = To_JObject(env, context_ptr, expected_type, val);
    return result;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1getGlobalObject(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue global_obj = JS_GetGlobalObject(ctx);
    return TO_JAVA_OBJECT(env, ctx, global_obj);
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1initNewJSObject(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue jsValue = JS_NewObject(ctx);
    return TO_JAVA_OBJECT(env, ctx, jsValue);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1initNewJSArray(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue jsValue = JS_NewArray(ctx);
    return TO_JAVA_OBJECT(env, ctx, jsValue);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1release(JNIEnv *env, jclass clazz, jlong context_ptr,
                                           jobject object_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JS_FreeValue(ctx, this_obj);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1get(JNIEnv *env, jclass clazz, jlong context_ptr,
                                       int expected_type,
                                       jobject object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyStr(ctx, this_obj, key_);
    return To_JObject(env, context_ptr, expected_type, result);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1getValue(JNIEnv *env, jclass clazz, jlong context_ptr,
                                            jobject object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyStr(ctx, this_obj, key_);
    return TO_JAVA_OBJECT(env, ctx, result);
}



extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1arrayGet(JNIEnv *env, jclass clazz, jlong context_ptr,
                                            int expected_type,
                                            jobject object_handle, jint index) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyUint32(ctx, this_obj, index);
    return To_JObject(env, context_ptr, expected_type, result);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1arrayGetValue(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jobject object_handle, jint index) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyUint32(ctx, this_obj, index);
    return TO_JAVA_OBJECT(env, ctx, result);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_android_QuickJS__1contains(JNIEnv *env, jclass clazz, jlong context_ptr,
                                            jobject object_handle, jstring key) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    JSAtom atom = JS_NewAtom(ctx, key_);
    int result = JS_HasProperty(ctx, this_obj, atom);
    JS_FreeAtom(ctx, atom);
    return result;
}extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_quickjs_android_QuickJS__1getKeys(JNIEnv *env, jclass clazz, jlong context_ptr,
                                           jobject object_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSPropertyEnum *tab;
    uint32_t len;
    JS_GetOwnPropertyNames(ctx, &tab, &len, this_obj, JS_GPN_STRING_MASK | JS_GPN_ENUM_ONLY);
    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray stringArray = env->NewObjectArray(len, strClass, nullptr);
    for (int i = 0; i < len; ++i) {
        jstring key = env->NewStringUTF(JS_AtomToCString(ctx, tab[i].atom));
        env->SetObjectArrayElement(stringArray, i, key);
    }
    return stringArray;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeFunction2(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                    jint expected_type, jobject object_handle,
                                                    jobject functionHandle,
                                                    jobject parameters_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue func_obj = TO_JS_VALUE(env, functionHandle);
    JSValue *argv = nullptr;
    int argc = 0;
    if (parameters_handle != nullptr) {
        JSValue argArray = TO_JS_VALUE(env, parameters_handle);
        argc = JS_VALUE_GET_INT(JS_GetPropertyStr(ctx, argArray, "length"));
        argv = new JSValue[argc];
        for (int i = 0; i < argc; ++i) {
            argv[i] = JS_GetPropertyUint32(ctx, argArray, i);
        }
    }
    JSValue result = JS_Call(ctx, func_obj, this_obj, argc, argv);
    delete argv;
    return To_JObject(env, context_ptr, expected_type, result);
}

JSValue executeJSFunction(JNIEnv *env,
                          jlong context_ptr, jobject object_handle,
                          jstring name, jobject parameters_handle) {
    const char *name_ = env->GetStringUTFChars(name, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue func_obj = JS_GetPropertyStr(ctx, this_obj, name_);
    JSValue *argv = nullptr;
    int argc = 0;
    if (parameters_handle != nullptr) {
        JSValue argArray = TO_JS_VALUE(env, parameters_handle);
        argc = JS_VALUE_GET_INT(JS_GetPropertyStr(ctx, argArray, "length"));
        argv = new JSValue[argc];
        for (int i = 0; i < argc; ++i) {
            argv[i] = JS_GetPropertyUint32(ctx, argArray, i);
        }
    }
    JSValue result = JS_Call(ctx, func_obj, this_obj, argc, argv);
    delete argv;
    return result;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1executeFunction(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                   jint expected_type, jobject object_handle,
                                                   jstring name, jobject parameters_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue result = executeJSFunction(env, context_ptr, object_handle, name, parameters_handle);
    jobject jResult = To_JObject(env, context_ptr, expected_type, result);
    if (!env->IsInstanceOf(jResult, jsValueCls)) {
        JS_FreeValue(ctx, result);
    }
    return jResult;
}

JSValue
callJavaCallback(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv, int magic,
                 JSValue *func_data) {
    JNIEnv *env;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    JSValue func = JS_GetPropertyUint32(ctx, *func_data, 0);
    JSValue args = JS_NewArray(ctx);
    if (argv != nullptr) {
        for (int i = 0; i < argc; ++i) {
            JSValue it = argv[i];
            JS_SetPropertyUint32(ctx, args, i, it);
        }
    }
    jobject objectHandle = TO_JAVA_OBJECT(env, ctx, this_val);
    jobject functionHandle = TO_JAVA_OBJECT(env, ctx, func);
    jobject argsHandle = TO_JAVA_OBJECT(env, ctx, args);
    jobject result = env->CallStaticObjectMethod(quickJSCls, callJavaCallbackMethodID,
                                                 objectHandle,
                                                 functionHandle,
                                                 argsHandle);
    if (result == nullptr) {
        return JS_NULL;
    } else if (env->IsInstanceOf(result, integerCls)) {
        return JS_NewInt32(ctx, env->CallIntMethod(result, intValueMethodID));
    } else if (env->IsInstanceOf(result, doubleCls)) {
        return JS_NewFloat64(ctx, env->CallDoubleMethod(result, doubleValueMethodID));
    } else if (env->IsInstanceOf(result, booleanCls)) {
        return JS_NewFloat64(ctx, env->CallBooleanMethod(result, booleanValueMethodID));
    } else if (env->IsInstanceOf(result, longCls)) {
        return JS_NewInt64(ctx, env->CallLongMethod(result, longValueMethodID));
    } else if (env->IsInstanceOf(result, stringCls)) {
        return JS_NewString(ctx, env->GetStringUTFChars((jstring) result, nullptr));
    }
    return JS_NULL;
}

JSValue
callJavaVoidCallback(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv, int magic,
                     JSValue *func_data) {
    JNIEnv *env;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    JSValue func = JS_GetPropertyUint32(ctx, *func_data, 0);
    JSValue args = JS_NewArray(ctx);
    if (argv != nullptr) {
        for (int i = 0; i < argc; ++i) {
            JSValue it = argv[i];
            JS_SetPropertyUint32(ctx, args, i, it);
        }
    }
    jobject objectHandle = TO_JAVA_OBJECT(env, ctx, this_val);
    jobject functionHandle = TO_JAVA_OBJECT(env, ctx, func);
    jobject argsHandle = TO_JAVA_OBJECT(env, ctx, args);
    env->CallStaticVoidMethod(quickJSCls, callJavaVoidCallbackMethodID,
                              objectHandle,
                              functionHandle,
                              argsHandle);
    return JS_NULL;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1initNewJSFunction(JNIEnv *env,
                                                     jclass clazz,
                                                     jlong context_ptr,
                                                     jboolean void_method) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSCFunctionData *functionData = void_method ? callJavaVoidCallback : callJavaCallback;
    JSValue func_data = JS_NewArray(ctx);
    JSValue func = JS_NewCFunctionData(ctx, functionData, 1, 0, 1, &func_data);
    JS_SetPropertyUint32(ctx, func_data, 0, func);
    return TO_JAVA_OBJECT(env, ctx, func);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1registerJavaMethod(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                      jobject object_handle, jstring function_name,
                                                      jboolean void_method) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSCFunctionData *functionData = void_method ? callJavaVoidCallback : callJavaCallback;
    JSValue func_data = JS_NewArray(ctx);
    JSValue func = JS_NewCFunctionData(ctx, functionData, 1, 0, 1, &func_data);
    JS_SetPropertyUint32(ctx, func_data, 0, func);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JS_SetPropertyStr(ctx, this_obj, env->GetStringUTFChars(function_name, nullptr), func);
    return TO_JAVA_OBJECT(env, ctx, func);
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_quickjs_android_QuickJS__1getObjectType(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jobject object_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue value = TO_JS_VALUE(env, object_handle);
    return GetObjectType(ctx, value);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1set(JNIEnv *env, jclass clazz, jlong context_ptr,
                                       jobject object_handle, jstring key, jobject value) {
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    if (value == nullptr) {
        JS_SetPropertyStr(ctx, this_obj, key_, JS_NULL);
    } else if (env->IsInstanceOf(value, integerCls)) {
        int value_ = env->CallIntMethod(value, intValueMethodID);
        JS_SetPropertyStr(ctx, this_obj, key_, JS_NewInt32(ctx, value_));
    } else if (env->IsInstanceOf(value, longCls)) {
        long value_ = env->CallLongMethod(value, longValueMethodID);
        JS_SetPropertyStr(ctx, this_obj, key_, JS_NewInt64(ctx, value_));
    } else if (env->IsInstanceOf(value, doubleCls)) {
        double value_ = env->CallDoubleMethod(value, doubleValueMethodID);
        JS_SetPropertyStr(ctx, this_obj, key_, JS_NewFloat64(ctx, value_));
    } else if (env->IsInstanceOf(value, booleanCls)) {
        bool value_ = env->CallBooleanMethod(value, booleanValueMethodID);
        JS_SetPropertyStr(ctx, this_obj, key_, JS_NewBool(ctx, value_));
    } else if (env->IsInstanceOf(value, stringCls)) {
        const char *value_ = env->GetStringUTFChars((jstring) value, nullptr);
        JS_SetPropertyStr(ctx, this_obj, key_, JS_NewString(ctx, value_));
    } else if (env->IsInstanceOf(value, jsValueCls)) {
        JS_SetPropertyStr(ctx, this_obj, key_, TO_JS_VALUE(env, value));
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_android_QuickJS__1arrayAdd(JNIEnv *env, jclass clazz, jlong context_ptr,
                                            jobject object_handle, jobject value) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    int len = getArrayLength(ctx, this_obj);
    if (value == nullptr) {
        JS_SetPropertyUint32(ctx, this_obj, len, JS_NULL);
    } else if (env->IsInstanceOf(value, integerCls)) {
        int value_ = env->CallIntMethod(value, intValueMethodID);
        JS_SetPropertyUint32(ctx, this_obj, len, JS_NewInt32(ctx, value_));
    } else if (env->IsInstanceOf(value, longCls)) {
        long value_ = env->CallLongMethod(value, longValueMethodID);
        JS_SetPropertyUint32(ctx, this_obj, len, JS_NewInt64(ctx, value_));
    } else if (env->IsInstanceOf(value, doubleCls)) {
        double value_ = env->CallDoubleMethod(value, doubleValueMethodID);
        JS_SetPropertyUint32(ctx, this_obj, len, JS_NewFloat64(ctx, value_));
    } else if (env->IsInstanceOf(value, booleanCls)) {
        bool value_ = env->CallBooleanMethod(value, booleanValueMethodID);
        JS_SetPropertyUint32(ctx, this_obj, len, JS_NewBool(ctx, value_));
    } else if (env->IsInstanceOf(value, stringCls)) {
        const char *value_ = env->GetStringUTFChars((jstring) value, nullptr);
        JS_SetPropertyUint32(ctx, this_obj, len, JS_NewString(ctx, value_));
    } else if (env->IsInstanceOf(value, jsValueCls)) {
        JS_SetPropertyUint32(ctx, this_obj, len, TO_JS_VALUE(env, value));
    }
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_android_QuickJS__1isUndefined(JNIEnv *env, jclass clazz, jlong context_ptr,
                                               jobject js_value) {
    JSValue value = TO_JS_VALUE(env, js_value);
    return JS_IsUndefined(value);
}extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_android_QuickJS__1Undefined(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    return TO_JAVA_OBJECT(env, ctx, JS_UNDEFINED);
}