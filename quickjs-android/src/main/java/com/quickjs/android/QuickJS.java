package com.quickjs.android;

public class QuickJS {
    private long runtimePtr;

    private QuickJS(long runtimePtr) {
        this.runtimePtr = runtimePtr;
    }

    public static QuickJS createV8Runtime() {
        return new QuickJS(_createRuntime());
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

    static native long _createRuntime();

    static native void _releaseRuntime(long runtimePtr);

    static native long _createContext(long runtimePtr);

    static native void _releaseContext(long contextPtr);

    static native int _executeIntegerScript(long contextPtr, String source, String fileName);

    static native double _executeDoubleScript(long contextPtr, String source, String fileName);

    static native String _executeStringScript(long contextPtr, String source, String fileName);

    static native boolean _executeBooleanScript(long contextPtr, String source, String fileName);

    static native Object _executeScript(long contextPtr, String source, String fileName);

    static native void _executeVoidScript(long contextPtr, String source, String fileName);

    static native long _getGlobalObject(long contextPtr);

    static native void _add(long contextPtr, long objectHandle, String key, int value);

    static native void _add(long contextPtr, long objectHandle, String key, double value);

    static native void _add(long contextPtr, long objectHandle, String key, boolean value);

    static native void _add(long contextPtr, long objectHandle, String key, String value);

    static native void _addObject(long contextPtr, long objectHandle, String key, long value);

    static native int _getInteger(long contextPtr, long objectHandle, String key);

    static native boolean _getBoolean(long contextPtr, long objectHandle, String key);

    static native double _getDouble(long contextPtr, long objectHandle, String key);

    static native String _getString(long contextPtr, long objectHandle, String key);

    static native String _arrayGetString(long contextPtr, long objectHandle, int index);

    static native double _arrayGetDouble(long contextPtr, long objectHandle, int index);

    static native boolean _arrayGetBoolean(long contextPtr, long objectHandle, int index);

    static native int _arrayGetInteger(long contextPtr, long objectHandle, int index);

    static native Object _executeFunction(long contextPtr, int expectedType, long objectHandle, String name, long parametersHandle);

    static native long _initNewJSObject(long contextPtr);

    static native void _release(long contextPtr, long objectHandle);


    static {
        System.loadLibrary("quickjs");
        System.loadLibrary("quickjs-android");
    }
}
