package com.quickjs;

class QuickJSNativeImpl implements QuickJSNative {

    static native long _createRuntime();

    @Override
    public native void _releaseRuntime(long runtimePtr);

    @Override
    public native long _createContext(long runtimePtr);

    @Override
    public native void _releaseContext(long contextPtr);

    @Override
    public native Object _executeScript(long contextPtr, int expectedType, String source, String fileName, int eval_flags);

    @Override
    public native JSObject _getGlobalObject(long contextPtr);

    @Override
    public native void _set(long contextPtr, JSValue objectHandle, String key, Object value);

    @Override
    public native Object _get(long contextPtr, int expectedType, JSValue objectHandle, String key);

    @Override
    public native Object _arrayGet(long contextPtr, int expectedType, JSValue objectHandle, int index);

    @Override
    public native void _arrayAdd(long contextPtr, JSValue objectHandle, Object value);

    @Override
    public native Object _executeFunction(long contextPtr, int expectedType, JSValue objectHandle, String name, JSValue parametersHandle);

    @Override
    public native Object _executeFunction2(long contextPtr, int expectedType, JSValue objectHandle, JSValue functionHandle, JSValue parametersHandle);

    @Override
    public native JSObject _initNewJSObject(long contextPtr);

    @Override
    public native JSArray _initNewJSArray(long contextPtr);

    @Override
    public native JSFunction _initNewJSFunction(long contextPtr, int javaCallerId, boolean voidMethod);

    @Override
    public native void _releasePtr(long contextPtr, long tag, int u_int32, double u_float64, long u_ptr);

    @Override
    public native JSFunction _registerJavaMethod(long contextPtr, JSValue objectHandle, String jsFunctionName, int javaCallerId, boolean voidMethod);

    @Override
    public native int _getObjectType(long contextPtr, JSValue objectHandle);

    @Override
    public native boolean _contains(long contextPtr, JSValue objectHandle, String key);

    @Override
    public native String[] _getKeys(long contextPtr, JSValue objectHandle);

    @Override
    public native boolean _isUndefined(long contextPtr, JSValue value);

    @Override
    public native JSValue _Undefined(long contextPtr);

    @Override
    public native JSValue _getValue(long contextPtr, JSObject object, String key);

    @Override
    public native JSValue _arrayGetValue(long contextPtr, JSArray array, int index);

    @Override
    public native String[] _getException(long contextPtr);

    @Override
    public native JSFunction _newClass(long contextPtr, int javaCallerId);

}
