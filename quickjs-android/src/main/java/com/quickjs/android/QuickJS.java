package com.quickjs.android;

import androidx.annotation.Keep;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

@Keep
public class QuickJS implements Closeable {
    private final long runtimePtr;
    static final Map<Long, JSContext> sContextMap = new HashMap<>();

    private QuickJS(long runtimePtr) {
        this.runtimePtr = runtimePtr;
    }

    public static QuickJS createRuntime() {
        return new QuickJS(_createRuntime());
    }


    static Object executeScript(JSContext context, int expectedType, String source, String fileName) {
        return _executeScript(context.getContextPtr(), expectedType, source, fileName);
    }


    public JSContext createContext() {
        JSContext context = new JSContext(_createContext(runtimePtr));
        sContextMap.put(context.getContextPtr(), context);
        return context;
    }

    public void close() {
        _releaseRuntime(runtimePtr);
    }

    static class MethodDescriptor {
        public JavaVoidCallback voidCallback;
        public JavaCallback callback;
    }


    @Keep
    static void callJavaVoidCallback(long contextPtr, JSValue objectHandle, JSValue functionHandle, JSArray argsHandle) {
        JSContext context = sContextMap.get(contextPtr);
        if (context == null) {
            return;
        }
        MethodDescriptor methodDescriptor = context.functionRegistry.get(functionHandle.tag);
        if (methodDescriptor == null) return;
        JSObject receiver = null;
        if (objectHandle instanceof JSObject) {
            receiver = (JSObject) objectHandle;
        }
        methodDescriptor.voidCallback.invoke(receiver, argsHandle);
    }

    @Keep
    static Object callJavaCallback(long contextPtr, JSValue objectHandle, JSValue functionHandle, JSArray argsHandle) {
        JSContext context = sContextMap.get(contextPtr);
        if (context == null) {
            return null;
        }
        MethodDescriptor methodDescriptor = context.functionRegistry.get(functionHandle.tag);
        if (methodDescriptor == null) return null;
        JSObject receiver = null;
        if (objectHandle instanceof JSObject) {
            receiver = (JSObject) objectHandle;
        }
        return methodDescriptor.callback.invoke(receiver, argsHandle);
    }

    @Keep
    static JSValue createJSValue(long contextPtr, int type, long tag, int u_int32, double u_float64, long u_ptr) {
        JSContext context = sContextMap.get(contextPtr);
        switch (type) {
            case JSValue.TYPE_JS_FUNCTION:
                return new JSFunction(context, tag, u_int32, u_float64, u_ptr);
            case JSValue.TYPE_JS_ARRAY:
                return new JSArray(context, tag, u_int32, u_float64, u_ptr);
            case JSValue.TYPE_JS_OBJECT:
                return new JSObject(context, tag, u_int32, u_float64, u_ptr);
            case JSValue.TYPE_UNDEFINED:
                return new JSObject.Undefined(context, tag, u_int32, u_float64, u_ptr);
            default:
                return new JSValue(context, tag, u_int32, u_float64, u_ptr);
        }
    }

    static Object executeJSFunction(JSContext context, JSValue objectHandle, String name, Object[] parameters) {
        JSArray args = new JSArray(context);
        if (parameters != null) {
            for (Object item : parameters) {
                if (item instanceof Integer) {
                    args.push((int) item);
                } else if (item instanceof Double) {
                    args.push((double) item);
                } else if (item instanceof Boolean) {
                    args.push((boolean) item);
                } else if (item instanceof String) {
                    args.push((String) item);
                } else if (item instanceof JSValue) {
                    args.push((JSValue) item);
                } else {
                    args.push((JSValue) null);
                }
            }
        }
        return _executeFunction(context.getContextPtr(), JSValue.TYPE_UNKNOWN, objectHandle, name, args);
    }

    static native long _createRuntime();

    static native void _releaseRuntime(long runtimePtr);

    static native long _createContext(long runtimePtr);

    static native void _releaseContext(long contextPtr);

    private static native Object _executeScript(long contextPtr, int expectedType, String source, String fileName);

    static native JSObject _getGlobalObject(long contextPtr);

    static native void _set(long contextPtr, JSValue objectHandle, String key, Object value);

    static native Object _get(long contextPtr, int expectedType, JSValue objectHandle, String key);

    static native Object _arrayGet(long contextPtr, int expectedType, JSValue objectHandle, int index);

    static native void _arrayAdd(long contextPtr, JSValue objectHandle, Object value);

    static native Object _executeFunction(long contextPtr, int expectedType, JSValue objectHandle, String name, JSValue parametersHandle);

    static native Object _executeFunction2(long contextPtr, int expectedType, JSValue objectHandle, JSValue functionHandle, JSValue parametersHandle);

    static native JSObject _initNewJSObject(long contextPtr);

    static native JSArray _initNewJSArray(long contextPtr);

    native static JSFunction _initNewJSFunction(long contextPtr, boolean voidMethod);

    static native void _release(long contextPtr, JSValue objectHandle);

    static native JSFunction _registerJavaMethod(long contextPtr, JSValue objectHandle, String jsFunctionName, boolean voidMethod);

    native static int _getObjectType(long contextPtr, JSValue objectHandle);

    native static boolean _contains(long contextPtr, JSValue objectHandle, String key);

    native static String[] _getKeys(long contextPtr, JSValue objectHandle);

    native static boolean _isUndefined(long contextPtr, JSValue jsValue);

    native static JSValue _Undefined(long contextPtr);

    native static JSValue _getValue(long contextPtr, JSObject jsObject, String key);

    native static JSValue _arrayGetValue(long contextPtr, JSArray jsArray, int index);


    static {
        System.loadLibrary("quickjs");
        System.loadLibrary("quickjs-android");
    }
}
