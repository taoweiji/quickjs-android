package com.quickjs;

public interface QuickJSNative {
    void _releaseRuntime(long runtimePtr);

    long _createContext(long runtimePtr);

    void _releaseContext(long contextPtr);

    Object _executeScript(long contextPtr, int expectedType, String source, String fileName, int eval_flags);

    JSObject _getGlobalObject(long contextPtr);

    void _set(long contextPtr, JSValue objectHandle, String key, Object value);

    Object _get(long contextPtr, int expectedType, JSValue objectHandle, String key);

    Object _arrayGet(long contextPtr, int expectedType, JSValue objectHandle, int index);

    void _arrayAdd(long contextPtr, JSValue objectHandle, Object value);

    Object _executeFunction(long contextPtr, int expectedType, JSValue objectHandle, String name, JSValue parametersHandle);

    Object _executeFunction2(long contextPtr, int expectedType, JSValue objectHandle, JSValue functionHandle, JSValue parametersHandle);

    JSObject _initNewJSObject(long contextPtr);

    JSArray _initNewJSArray(long contextPtr);

    JSFunction _initNewJSFunction(long contextPtr, int javaCallerId, boolean voidMethod);

    void _releasePtr(long contextPtr, long tag, int u_int32, double u_float64, long u_ptr);

    JSFunction _registerJavaMethod(long contextPtr, JSValue objectHandle, String jsFunctionName, int javaCallerId, boolean voidMethod);

    int _getObjectType(long contextPtr, JSValue objectHandle);

    boolean _contains(long contextPtr, JSValue objectHandle, String key);

    String[] _getKeys(long contextPtr, JSValue objectHandle);

    boolean _isUndefined(long contextPtr, JSValue value);

    JSValue _Undefined(long contextPtr);

    JSValue _getValue(long contextPtr, JSObject object, String key);

    JSValue _arrayGetValue(long contextPtr, JSArray array, int index);

    String[] _getException(long contextPtr);

    JSFunction _newClass(long contextPtr, int javaCallerId);
}
