package com.quickjs.android;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JSContext extends JSObject {
    private final long contextPtr;
    Map<Long, QuickJS.MethodDescriptor> functionRegistry = new HashMap<>();
    private final Set<JSValue> refs = new HashSet<>();

    JSContext(long contextPtr) {
        super(null, QuickJS._getGlobalObject(contextPtr));
        this.contextPtr = contextPtr;
        this.context = this;
    }

    long getContextPtr() {
        return this.contextPtr;
    }

    void addObjRef(JSValue reference) {
        if (reference.getClass() != JSValue.class) {
            refs.add(reference);
        }
    }

    void releaseObjRef(JSValue reference) {
        refs.remove(reference);
    }

    @Override
    public void close() {
        super.close();
        JSValue[] arr = new JSValue[refs.size()];
        refs.toArray(arr);
        for (JSValue it : arr) {
            it.close();
        }
        QuickJS._releaseContext(contextPtr);
        QuickJS.sContextMap.remove(getContextPtr());
    }

    public Object executeScript(int expectedType, String source, String fileName) {
        return QuickJS.executeScript(this, expectedType, source, fileName);
    }

    public Object executeScript(String source, String fileName) {
        return executeScript(JSValue.TYPE_UNKNOWN, source, fileName);
    }

    public int executeIntegerScript(String source, String fileName) {
        return (int) executeScript(JSValue.TYPE_INTEGER, source, fileName);
    }

    public double executeDoubleScript(String source, String fileName) {
        return (double) executeScript(JSValue.TYPE_DOUBLE, source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) {
        return (boolean) executeScript(JSValue.TYPE_BOOLEAN, source, fileName);
    }

    public String executeStringScript(String source, String fileName) {
        return (String) executeScript(JSValue.TYPE_STRING, source, fileName);
    }

    public void executeVoidScript(String source, String fileName) {
        executeScript(JSValue.TYPE_NULL, source, fileName);
    }

    public JSArray executeArrayScript(String source, String fileName) {
        return (JSArray) executeScript(JSValue.TYPE_JS_ARRAY, source, fileName);
    }

    public JSObject executeObjectScript(String source, String fileName) {
        return (JSObject) executeScript(JSValue.TYPE_JS_OBJECT, source, fileName);
    }

    void registerCallback(JavaCallback callback, JSValue objectHandle, String jsFunctionName) {
        JSFunction functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), objectHandle, jsFunctionName, false);
        registerCallback(callback, functionHandle);
    }

    void registerCallback(JavaVoidCallback callback, JSValue objectHandle, String jsFunctionName) {
        JSFunction functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), objectHandle, jsFunctionName, true);
        registerCallback(callback, functionHandle);
    }

    void registerCallback(JavaCallback callback, JSFunction functionHandle) {
        QuickJS.MethodDescriptor methodDescriptor = new QuickJS.MethodDescriptor();
        methodDescriptor.callback = callback;
        functionRegistry.put(functionHandle.tag, methodDescriptor);
    }

    void registerCallback(JavaVoidCallback callback, JSFunction functionHandle) {
        QuickJS.MethodDescriptor methodDescriptor = new QuickJS.MethodDescriptor();
        methodDescriptor.voidCallback = callback;
        functionRegistry.put(functionHandle.tag, methodDescriptor);
    }

}
