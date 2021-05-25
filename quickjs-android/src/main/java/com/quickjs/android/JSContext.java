package com.quickjs.android;

import java.util.HashMap;
import java.util.Map;

public class JSContext extends JSObject {
    private final long contextPtr;

//    Map<Long, MethodDescriptor> functionRegistry = new HashMap<>();

    JSContext(long contextPtr) {
        super();
        this.contextPtr = contextPtr;
        this.objectHandle = QuickJS._getGlobalObject(contextPtr);
        this.context = this;
    }

    long getContextPtr() {
        return this.contextPtr;
    }

    @Override
    public void close() {
        super.close();
        QuickJS._releaseContext(contextPtr);
    }

    public Object executeScript(int expectedType, String source, String fileName) {
        return QuickJS.executeScript(this, expectedType, source, fileName);
    }

    public Object executeScript(String source, String fileName) {
        return executeScript(JSValue.UNKNOWN, source, fileName);
    }

    public int executeIntegerScript(String source, String fileName) {
        return (int) executeScript(JSValue.INTEGER, source, fileName);
    }

    public double executeDoubleScript(String source, String fileName) {
        return (double) executeScript(JSValue.DOUBLE, source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) {
        return (boolean) executeScript(JSValue.BOOLEAN, source, fileName);
    }

    public String executeStringScript(String source, String fileName) {
        return (String) executeScript(JSValue.STRING, source, fileName);
    }

    public void executeVoidScript(String source, String fileName) {
        executeScript(JSValue.NULL, source, fileName);
    }

    public JSArray executeArrayScript(String source, String fileName) {
        return (JSArray) executeScript(JSValue.JS_ARRAY, source, fileName);
    }

    public JSObject executeObjectScript(String source, String fileName) {
        return (JSObject) executeScript(JSValue.JS_OBJECT, source, fileName);
    }

    void registerCallback(JavaCallback callback, long objectHandle, String jsFunctionName) {
        long functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), objectHandle, jsFunctionName, false);
        registerCallback(callback, functionHandle);
    }

    void registerCallback(JavaVoidCallback callback, long objectHandle, String jsFunctionName) {
        long functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), objectHandle, jsFunctionName, true);
        registerCallback(callback, functionHandle);
    }

    void registerCallback(JavaCallback callback, long functionHandle) {
        MethodDescriptor methodDescriptor = new MethodDescriptor();
        methodDescriptor.callback = callback;
        QuickJS.functionRegistry.put(functionHandle, methodDescriptor);
    }

    void registerCallback(JavaVoidCallback callback, long functionHandle) {
        MethodDescriptor methodDescriptor = new MethodDescriptor();
        methodDescriptor.voidCallback = callback;
        QuickJS.functionRegistry.put(functionHandle, methodDescriptor);
    }

    long initNewJSObject(long contextPtr) {
        return QuickJS._initNewJSObject(contextPtr);
    }

}
