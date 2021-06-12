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
jmethodID callJavaCallbackMethodID = nullptr;
jmethodID createJSValueMethodID = nullptr;
jmethodID getModuleScriptMethodID = nullptr;
jmethodID convertModuleNameMethodID = nullptr;

jclass jsValueCls = nullptr;
jfieldID js_value_tag_id;
jfieldID js_value_u_int32_id;
jfieldID js_value_u_float64_id;
jfieldID js_value_u_ptr_id;

void initES6Module(JSRuntime *rt);

bool JS_Equals(JSValue v1, JSValue v2) {
#if defined(JS_NAN_BOXING)
    return v1 == v2;
#else
    return v1.tag == v2.tag && v1.u.int32 == v2.u.int32 && v1.u.float64 == v2.u.float64 && v1.u.ptr == v2.u.ptr;
#endif
}

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

JSValue JobjectToJSValue(JNIEnv *env, JSContext *ctx, jobject value) {
    if (value == nullptr) {
        return JS_NULL;
    } else if (env->IsInstanceOf(value, integerCls)) {
        return JS_NewInt32(ctx, env->CallIntMethod(value, intValueMethodID));
    } else if (env->IsInstanceOf(value, longCls)) {
        return JS_NewInt64(ctx, env->CallLongMethod(value, longValueMethodID));
    } else if (env->IsInstanceOf(value, doubleCls)) {
        return JS_NewFloat64(ctx, env->CallDoubleMethod(value, doubleValueMethodID));
    } else if (env->IsInstanceOf(value, booleanCls)) {
        return JS_NewBool(ctx, env->CallBooleanMethod(value, booleanValueMethodID));
    } else if (env->IsInstanceOf(value, stringCls)) {
        return JS_NewString(ctx, env->GetStringUTFChars((jstring) value, nullptr));
    } else if (env->IsInstanceOf(value, jsValueCls)) {
        return JS_DupValue(ctx, TO_JS_VALUE(env, value));
    }
    return JS_UNDEFINED;
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
        return TYPE_DOUBLE;
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
    quickJSCls = (jclass) env->NewGlobalRef((env)->FindClass("com/quickjs/QuickJS"));

    integerInitMethodID = env->GetMethodID(integerCls, "<init>", "(I)V");
    longInitMethodID = env->GetMethodID(longCls, "<init>", "(J)V");
    doubleInitMethodID = env->GetMethodID(doubleCls, "<init>", "(D)V");
    booleanInitMethodID = env->GetMethodID(booleanCls, "<init>", "(Z)V");

    callJavaCallbackMethodID = env->GetStaticMethodID(quickJSCls, "callJavaCallback",
                                                      "(JILcom/quickjs/JSValue;Lcom/quickjs/JSArray;Z)Ljava/lang/Object;");

    createJSValueMethodID = env->GetStaticMethodID(quickJSCls, "createJSValue",
                                                   "(JIJIDJ)Lcom/quickjs/JSValue;");
    getModuleScriptMethodID = env->GetStaticMethodID(quickJSCls, "getModuleScript",
                                                     "(JLjava/lang/String;)Ljava/lang/String;");
    convertModuleNameMethodID = env->GetStaticMethodID(quickJSCls, "convertModuleName",
                                                       "(JLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;");

    intValueMethodID = env->GetMethodID(integerCls, "intValue", "()I");
    longValueMethodID = env->GetMethodID(longCls, "longValue", "()J");
    doubleValueMethodID = env->GetMethodID(doubleCls, "doubleValue", "()D");
    booleanValueMethodID = env->GetMethodID(booleanCls, "booleanValue", "()Z");


    jsValueCls = (jclass) env->NewGlobalRef((env)->FindClass("com/quickjs/JSValue"));
    js_value_tag_id = env->GetFieldID(jsValueCls, "tag", "J");
    js_value_u_int32_id = env->GetFieldID(jsValueCls, "u_int32", "I");
    js_value_u_float64_id = env->GetFieldID(jsValueCls, "u_float64", "D");
    js_value_u_ptr_id = env->GetFieldID(jsValueCls, "u_ptr", "J");
    return JNI_VERSION_1_6;
}

int GetArrayLength(JSContext *ctx, JSValue this_obj) {
    JSValue lenValue = JS_GetPropertyStr(ctx, this_obj, "length");
    return JS_VALUE_GET_INT(lenValue);
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_QuickJSNativeImpl__1createRuntime(JNIEnv *env, jclass clazz) {
    JSRuntime *runtime = JS_NewRuntime();
    initES6Module(runtime);
    return reinterpret_cast<jlong>(runtime);
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_quickjs_QuickJSNativeImpl__1createContext(JNIEnv *env, jclass clazz, jlong runtime_ptr) {
    auto *runtime = reinterpret_cast<JSRuntime *>(runtime_ptr);
    auto *ctx = JS_NewContext(runtime);
    return reinterpret_cast<jlong>(ctx);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_QuickJSNativeImpl__1releaseRuntime(JNIEnv *env, jclass clazz, jlong runtime_ptr) {
    auto *runtime = reinterpret_cast<JSRuntime *>(runtime_ptr);
    JS_FreeRuntime(runtime);
}extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_QuickJSNativeImpl__1releaseContext(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JS_FreeContext(ctx);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1executeScript(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                   jint expected_type,
                                                   jstring source, jstring file_name,
                                                   jint eval_flags) {
    if (source == nullptr) {
        return nullptr;
    }
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    const char *source_ = env->GetStringUTFChars(source, nullptr);
    const int source_length = env->GetStringUTFLength(source);
    const char *file_name_;
    if (file_name == nullptr) {
        file_name_ = "";
    } else {
        file_name_ = env->GetStringUTFChars(file_name, nullptr);
    }
    JSValue val = JS_Eval(ctx, source_, (size_t) source_length, file_name_, eval_flags);
    jobject result = To_JObject(env, context_ptr, expected_type, val);
    return result;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1getGlobalObject(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue global_obj = JS_GetGlobalObject(ctx);
    return TO_JAVA_OBJECT(env, ctx, global_obj);
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1initNewJSObject(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue jsValue = JS_NewObject(ctx);
    return TO_JAVA_OBJECT(env, ctx, jsValue);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1initNewJSArray(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue jsValue = JS_NewArray(ctx);
    return TO_JAVA_OBJECT(env, ctx, jsValue);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_QuickJSNativeImpl__1releasePtr(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                jlong tag,
                                                jint u_int32, jdouble u_float64, jlong u_ptr) {

    JSValue value;
#if defined(JS_NAN_BOXING)
    value = tag;
#else
    value.tag = tag;
    value.u.int32 = u_int32;
    value.u.float64 = u_float64;
    value.u.ptr = (void *) u_ptr;
#endif
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JS_FreeValue(ctx, value);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1get(JNIEnv *env, jclass clazz, jlong context_ptr,
                                         int expected_type,
                                         jobject object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyStr(ctx, this_obj, key_);
    jobject tmp = To_JObject(env, context_ptr, expected_type, result);
//    JS_FreeValue(ctx, result);
    return tmp;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1getValue(JNIEnv *env, jclass clazz, jlong context_ptr,
                                              jobject object_handle, jstring key) {
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyStr(ctx, this_obj, key_);
    return TO_JAVA_OBJECT(env, ctx, result);
}



extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1arrayGet(JNIEnv *env, jclass clazz, jlong context_ptr,
                                              int expected_type,
                                              jobject object_handle, jint index) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyUint32(ctx, this_obj, index);
    jobject jo = To_JObject(env, context_ptr, expected_type, result);
//    JS_FreeValue(ctx, result);
    return jo;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1arrayGetValue(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                   jobject object_handle, jint index) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue result = JS_GetPropertyUint32(ctx, this_obj, index);
    return TO_JAVA_OBJECT(env, ctx, result);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_QuickJSNativeImpl__1contains(JNIEnv *env, jclass clazz, jlong context_ptr,
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
Java_com_quickjs_QuickJSNativeImpl__1getKeys(JNIEnv *env, jclass clazz, jlong context_ptr,
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

JSValue executeFunction(JNIEnv *env, jlong context_ptr, jobject object_handle, JSValue func,
                        jobject parameters_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);

    JSValue *argv = nullptr;
    int argc = 0;
    if (parameters_handle != nullptr) {
        JSValue argArray = TO_JS_VALUE(env, parameters_handle);
        argc = GetArrayLength(ctx, argArray);
        argv = new JSValue[argc];
        for (int i = 0; i < argc; ++i) {
            argv[i] = JS_GetPropertyUint32(ctx, argArray, i);
        }
    }
    JSValue global = JS_GetGlobalObject(ctx);

    if (JS_Equals(this_obj, global)) {
        this_obj = JS_UNDEFINED;
    }
    JSValue result = JS_Call(ctx, func, this_obj, argc, argv);
    JS_FreeValue(ctx, func);
    JS_FreeValue(ctx, global);
    if (argv != nullptr) {
        for (int i = 0; i < argc; ++i) {
            JS_FreeValue(ctx, argv[i]);
        }
    }
//    JS_FreeValue(ctx, result);
    return result;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1executeFunction2(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                      jint expected_type, jobject object_handle,
                                                      jobject functionHandle,
                                                      jobject parameters_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue func_obj = TO_JS_VALUE(env, functionHandle);
    JS_DupValue(ctx, func_obj);
    JSValue value = executeFunction(env, context_ptr, object_handle, func_obj, parameters_handle);
    jobject result = To_JObject(env, context_ptr, expected_type, value);
    return result;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1executeFunction(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                     jint expected_type, jobject object_handle,
                                                     jstring name, jobject parameters_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JSValue func_obj = JS_GetPropertyStr(ctx, this_obj, env->GetStringUTFChars(name, nullptr));
    JSValue value = executeFunction(env, context_ptr, object_handle, func_obj, parameters_handle);
    jobject result = To_JObject(env, context_ptr, expected_type, value);
    return result;
}

JSValue
callJavaCallback(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv, int magic,
                 JSValue *func_data) {
    JNIEnv *env;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    int callbackId = JS_VALUE_GET_INT(func_data[0]);
    bool void_method = JS_VALUE_GET_BOOL(func_data[1]);
    auto context_ptr = (jlong) ctx;
    JSValue args = JS_NewArray(ctx);

    if (argv != nullptr) {
        for (int i = 0; i < argc; ++i) {
            JSValue it = argv[i];
            JS_SetPropertyUint32(ctx, args, i, JS_DupValue(ctx, it));
        }
    }
    jobject objectHandle = TO_JAVA_OBJECT(env, ctx, this_val);
    jobject argsHandle = TO_JAVA_OBJECT(env, ctx, args);
    JSValue global = JS_GetGlobalObject(ctx);
    if (!JS_Equals(global, this_val)) {
        JS_DupValue(ctx, this_val);
    }
    JS_FreeValue(ctx, global);
    jobject result = env->CallStaticObjectMethod(quickJSCls, callJavaCallbackMethodID,
                                                 context_ptr,
                                                 callbackId,
                                                 objectHandle,
                                                 argsHandle,
                                                 void_method
    );

    JSValue value = JobjectToJSValue(env, ctx, result);
    if (env->IsInstanceOf(result, jsValueCls)) {
//        JS_FreeValue(ctx,value);
//        JS_DupValue(ctx, value);
    }
    return value;
}

JSValue newFunction(jlong context_ptr, jboolean void_method, int callbackId) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValueConst func_data[2];
    func_data[0] = JS_NewInt32(ctx, callbackId);
    func_data[1] = JS_NewBool(ctx, void_method);
    JSValue func = JS_NewCFunctionData(ctx, callJavaCallback, 1, 0, 2, func_data);
    return func;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1initNewJSFunction(JNIEnv *env,
                                                       jclass clazz,
                                                       jlong context_ptr,
                                                       jint callbackId,
                                                       jboolean void_method) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue func = newFunction(context_ptr, void_method, callbackId);
    return TO_JAVA_OBJECT(env, ctx, func);
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1registerJavaMethod(JNIEnv *env, jclass clazz,
                                                        jlong context_ptr,
                                                        jobject object_handle,
                                                        jstring function_name,
                                                        jint callbackId,
                                                        jboolean void_method) {
    const char *name_ = env->GetStringUTFChars(function_name, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue func = newFunction(context_ptr, void_method, callbackId);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JS_SetPropertyStr(ctx, this_obj, name_, JS_DupValue(ctx, func));
    return TO_JAVA_OBJECT(env, ctx, func);
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_quickjs_QuickJSNativeImpl__1getObjectType(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                   jobject object_handle) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue value = TO_JS_VALUE(env, object_handle);
    return GetObjectType(ctx, value);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_QuickJSNativeImpl__1set(JNIEnv *env, jclass clazz, jlong context_ptr,
                                         jobject object_handle, jstring key, jobject value) {
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    JS_SetPropertyStr(ctx, this_obj, key_, JobjectToJSValue(env, ctx, value));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_quickjs_QuickJSNativeImpl__1arrayAdd(JNIEnv *env, jclass clazz, jlong context_ptr,
                                              jobject object_handle, jobject value) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue this_obj = TO_JS_VALUE(env, object_handle);
    int len = GetArrayLength(ctx, this_obj);
    JS_SetPropertyUint32(ctx, this_obj, len, JobjectToJSValue(env, ctx, value));
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_quickjs_QuickJSNativeImpl__1isUndefined(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                 jobject js_value) {
    JSValue value = TO_JS_VALUE(env, js_value);
    return JS_IsUndefined(value);
}extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1Undefined(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    return TO_JAVA_OBJECT(env, ctx, JS_UNDEFINED);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_quickjs_QuickJSNativeImpl__1getException(JNIEnv *env, jclass clazz, jlong context_ptr) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue exc = JS_GetException(ctx);
    if (!JS_IsError(ctx, exc)) {
        return nullptr;
    }

    JSValue func = JS_GetPropertyStr(ctx, exc, "toString");
    JSValue nameValue = JS_GetPropertyStr(ctx, exc, "name");
    JSValue stackValue = JS_GetPropertyStr(ctx, exc, "stack");
    JSValue titleValue = JS_Call(ctx, func, exc, 0, nullptr);
    JS_FreeValue(ctx, func);

    std::vector<const char *> messages;
    messages.push_back(JS_ToCString(ctx, nameValue));
    messages.push_back(JS_ToCString(ctx, titleValue));
    while (!JS_IsUndefined(stackValue)) {
        messages.push_back(JS_ToCString(ctx, stackValue));
        JS_FreeValue(ctx, stackValue);
        stackValue = JS_GetPropertyStr(ctx, stackValue, "stack");
    }
    JS_FreeValue(ctx, exc);

    jobjectArray stringArray = env->NewObjectArray(messages.size(), stringCls, nullptr);
    for (int i = 0; i < messages.size(); ++i) {
        jstring str = env->NewStringUTF(messages[i]);
        env->SetObjectArrayElement(stringArray, i, str);
    }
    return stringArray;
}

const char *GetModuleScript(JSContext *ctx, const char *module_name, int *scriptLen) {
    JNIEnv *env;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    jobject result = env->CallStaticObjectMethod(quickJSCls, getModuleScriptMethodID, (jlong) ctx,
                                                 env->NewStringUTF(module_name));
    if (result == nullptr) {
        return nullptr;
    }
    *scriptLen = env->GetStringUTFLength((jstring) result);
    return env->GetStringUTFChars((jstring) result, nullptr);
}

JSModuleDef *_JSModuleLoaderFunc(JSContext *ctx, const char *module_name, void *opaque) {
    int scriptLen;
    void *m;
    const char *script = GetModuleScript(ctx, module_name, &scriptLen);
    if (script == nullptr) {
        return nullptr;
    }
    JSValue func_val = JS_Eval(ctx, script, scriptLen, module_name,
                               JS_EVAL_TYPE_MODULE | JS_EVAL_FLAG_COMPILE_ONLY);
    m = JS_VALUE_GET_PTR(func_val);
    JS_FreeValue(ctx, func_val);
    return (JSModuleDef *) m;
}

char *_JSModuleNormalizeFunc(JSContext *ctx,
                             const char *module_base_name,
                             const char *module_name, void *opaque) {
    JNIEnv *env;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    jobject result = env->CallStaticObjectMethod(quickJSCls, convertModuleNameMethodID, (jlong) ctx,
                                                 env->NewStringUTF(module_base_name),
                                                 env->NewStringUTF(module_name));
    if (result == nullptr) {
        return nullptr;
    }
    return (char *) env->GetStringUTFChars((jstring) result, nullptr);
}

void initES6Module(JSRuntime *rt) {
    JS_SetModuleLoaderFunc(rt, _JSModuleNormalizeFunc, _JSModuleLoaderFunc, nullptr);
}

//JSValue js_worker_constructor(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv){
//
//}
JSValue js_worker_constructor(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv,
                              int me) {
    JNIEnv *env;
    jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
    auto context_ptr = (jlong) ctx;
    JSValue args = JS_NewArray(ctx);
    int callbackId = JS_VALUE_GET_INT(JS_GetPropertyStr(ctx, this_val, "java_caller_id"));

    if (argv != nullptr) {
        for (int i = 0; i < argc; ++i) {
            JSValue it = argv[i];
            JS_SetPropertyUint32(ctx, args, i, JS_DupValue(ctx, it));
        }
    }
    jobject objectHandle = TO_JAVA_OBJECT(env, ctx, this_val);
    jobject argsHandle = TO_JAVA_OBJECT(env, ctx, args);
    JSValue global = JS_GetGlobalObject(ctx);
    if (!JS_Equals(global, this_val)) {
        JS_DupValue(ctx, this_val);
    }
    JS_FreeValue(ctx, global);
    jobject result = env->CallStaticObjectMethod(quickJSCls, callJavaCallbackMethodID,
                                                 context_ptr,
                                                 callbackId,
                                                 objectHandle,
                                                 argsHandle,
                                                 false
    );
    JSValue value = JobjectToJSValue(env, ctx, result);
    return value;
}

void newWorker(JSContext *ctx, int callbackId) {
//    JSClassDef workerClassDef = new JSClassDef(.);
//    JSClassID workerJSClassID;
//    JS_NewClassID(&workerJSClassID);
//    JS_NewClass(rt,workerJSClassID,);
//    JS_SetModuleLoaderFunc(rt, _JSModuleNormalizeFunc, _JSModuleLoaderFunc, nullptr);
//    obj = JS_NewCFunction2(ctx, js_worker_constructor, "Worker", 1,
//                           JS_CFUNC_constructor, 0);

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_quickjs_QuickJSNativeImpl__1newClass(JNIEnv *env, jobject thiz, jlong context_ptr,
                                              jint java_caller_id) {
    auto *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSValue func = JS_NewCFunctionMagic(ctx, js_worker_constructor, "Worker", 1,
                                        JS_CFUNC_constructor, java_caller_id);
    JS_SetPropertyStr(ctx, func, "java_caller_id", JS_NewInt32(ctx, java_caller_id));
    return TO_JAVA_OBJECT(env, ctx, func);
}