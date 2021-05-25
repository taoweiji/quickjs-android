//
// Created by Wiki on 2021/5/26.
//
#include <jni.h>
#include <string>
#include <quickjs/quickjs.h>

JSValue java_method_call(JSContext *ctx, JSValueConst func_obj,
                         JSValueConst this_val, int argc, JSValueConst *argv,
                         int flags) {

}

void java_method_finalizer(JSRuntime *rt, JSValue val) {

}

static JSClassDef java_method_class = {
        "JavaMethod",
        .call = java_method_call,
        .finalizer = java_method_finalizer
};

extern "C"
JNIEXPORT jobject JNICALL
tttt(JNIEnv *env, jclass clazz, jlong context_ptr,
                                                     jlong object_handle, jstring name,
                                                     jobjectArray parameters) {
    JSContext *ctx = reinterpret_cast<JSContext *>(context_ptr);
    JSClassID *pclass_id;
    JS_NewClassID(pclass_id);
    JS_NewClass(JS_GetRuntime(ctx), *pclass_id, &java_method_class);

//    JS_NewCFunctionMagic()

    // TODO: implement _executeJSFunction()
    return nullptr;
}