package com.quickjs.android;

public class JSContext {
    private long contextPtr;
    private long objectHandle;

    JSContext(long contextPtr) {
        this.contextPtr = contextPtr;
        this.objectHandle = QuickJS._getGlobalObject(contextPtr);
    }

    long getContextPtr() {
        return this.contextPtr;
    }

    public int executeIntegerScript(String source, String fileName) {
        return QuickJS._executeIntegerScript(this.contextPtr, source, fileName);
    }

    public double executeDoubleScript(String source, String fileName) {
        return QuickJS._executeDoubleScript(this.contextPtr, source, fileName);
    }

    public String executeStringScript(String source, String fileName) {
        return QuickJS._executeStringScript(this.contextPtr, source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) {
        return QuickJS._executeBooleanScript(this.contextPtr, source, fileName);
    }

    public Object executeScript(String source, String fileName) {
        return QuickJS._executeScript(this.contextPtr, source, fileName);
    }

    public void executeVoidScript(String source, String fileName) {
        QuickJS._executeVoidScript(this.contextPtr, source, fileName);
    }

    public void executeArrayScript(String source, String fileName) {
        // TODO
    }


    public void close() {
        QuickJS._releaseContext(contextPtr);
    }

    public void add(String key, int value) {
        QuickJS._add(contextPtr, this.objectHandle, key, value);
    }

    public void add(String key, double value) {
        QuickJS._add(contextPtr, this.objectHandle, key, value);
    }

    public void add(String key, String value) {
        QuickJS._add(contextPtr, this.objectHandle, key, value);
    }

    public void add(String key, boolean value) {
        QuickJS._add(contextPtr, this.objectHandle, key, value);
    }

    public void add(String key, JSValue value) {
        QuickJS._addObject(contextPtr, this.objectHandle, key, value.objectHandle);
    }


    public JSObject registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        // TODO
        return null;
    }

    public JSObject registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
        // TODO
        return null;
    }

    public Object executeFunction(String name, JSArray parameters) {
        // TODO
        return null;
    }

    public int executeIntegerFunction(String name, JSArray parameters) {
        // TODO
        return 0;
    }

    public double executeDoubleFunction(String name, JSArray parameters) {
        // TODO
        return 0;
    }

    public String executeStringFunction(String name, JSArray parameters) {
        // TODO
        return null;
    }

    public boolean executeBooleanFunction(String name, JSArray parameters) {
        // TODO
        return false;
    }

    public JSArray executeArrayFunction(String name, JSArray parameters) {
        // TODO
        return null;
    }

    public JSObject executeObjectFunction(String name, JSArray parameters) {
        // TODO
        return null;
    }

    public void executeVoidFunction(String name, JSArray parameters) {
        // TODO
    }


    public Object executeJSFunction(String name) {
        // TODO
        return null;
    }

    public Object executeJSFunction(String name, Object... parameters) {
        // TODO
        return null;
    }

    public long initNewJSObject(long contextPtr) {
        return QuickJS._initNewJSObject(contextPtr);
    }
}
