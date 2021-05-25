package com.quickjs.android;

public class QuickJS {
    private long runtimePtr;

    private QuickJS(long runtimePtr) {
        this.runtimePtr = runtimePtr;
    }

    public static QuickJS createRuntime() {
        return new QuickJS(_createRuntime());
    }

    native static boolean _contains(long contextPtr, long objectHandle, String key);

    native static String[] _getKeys(long contextPtr, long objectHandle);

    static Object executeScript(JSContext jsContext, int expectedType, String source, String fileName) {
        Object object = _executeScript(jsContext.getContextPtr(), expectedType, source, fileName);
        return toJavaObject(jsContext, object, expectedType);
    }


    public JSContext createContext() {
        return new JSContext(_createContext(runtimePtr));
    }

    public void close() {
        _releaseRuntime(runtimePtr);
    }

    long getRuntimePtr() {
        return this.runtimePtr;
    }

    static Object executeFunction(JSContext context, int expectedType, long objectHandle, String name, long parametersHandle) {
        Object object = _executeFunction(context.getContextPtr(), expectedType, objectHandle, name, parametersHandle);
        return toJavaObject(context, object, expectedType);
    }

    static Object toJavaObject(JSContext context, Object object, int expectedType) {
        if (object == null) return null;
        switch (expectedType) {
            case JSValue.JS_ARRAY:
                return new JSArray(context, (long) object);
            case JSValue.JS_OBJECT:
                return new JSObject(context, (long) object);
        }
        return object;
    }


    static native long _createRuntime();

    static native void _releaseRuntime(long runtimePtr);

    static native long _createContext(long runtimePtr);

    static native void _releaseContext(long contextPtr);

    private static native Object _executeScript(long contextPtr, int expectedType, String source, String fileName);

    static native long _getGlobalObject(long contextPtr);

    static native void _set(long contextPtr, long objectHandle, String key, int value);

    static native void _set(long contextPtr, long objectHandle, String key, double value);

    static native void _set(long contextPtr, long objectHandle, String key, boolean value);

    static native void _set(long contextPtr, long objectHandle, String key, String value);

    static native void _setObject(long contextPtr, long objectHandle, String key, long value);

    static native int _getInteger(long contextPtr, long objectHandle, String key);

    static native boolean _getBoolean(long contextPtr, long objectHandle, String key);

    static native double _getDouble(long contextPtr, long objectHandle, String key);

    static native String _getString(long contextPtr, long objectHandle, String key);

    static native long _getObject(long contextPtr, long objectHandle, String key);

    static native String _arrayGetString(long contextPtr, long objectHandle, int index);

    static native double _arrayGetDouble(long contextPtr, long objectHandle, int index);

    static native boolean _arrayGetBoolean(long contextPtr, long objectHandle, int index);

    static native int _arrayGetInteger(long contextPtr, long objectHandle, int index);

    static native void _arrayAdd(long contextPtr, long objectHandle, int value);

    static native void _arrayAdd(long contextPtr, long objectHandle, double value);

    static native void _arrayAdd(long contextPtr, long objectHandle, boolean value);

    static native void _arrayAdd(long contextPtr, long objectHandle, String value);

    static native void _arrayAddObject(long contextPtr, long objectHandle, long value);

    private static native Object _executeFunction(long contextPtr, int expectedType, long objectHandle, String name, long parametersHandle);

    static native long _initNewJSObject(long contextPtr);

    static native long _initNewJSArray(long contextPtr);

    static native void _release(long contextPtr, long objectHandle);

    static native long _registerJavaMethod(long contextPtr, long objectHandle, String jsFunctionName, boolean voidMethod);

    static {
        System.loadLibrary("quickjs");
        System.loadLibrary("quickjs-android");
    }
}
