package com.quickjs.android;

import java.util.HashMap;
import java.util.Map;

public class JSContext extends JSObject {
    private final long contextPtr;

    Map<Long, MethodDescriptor> functionRegistry = new HashMap<>();

    JSContext(long contextPtr) {
        super();
        this.contextPtr = contextPtr;
        this.objectHandle = QuickJS._getGlobalObject(contextPtr);
    }

    long getContextPtr() {
        if (this.context != null) {
            return this.context.getContextPtr();
        }
        return this.contextPtr;
    }

    @Override
    public void close() {
        QuickJS._releaseContext(contextPtr);
    }

    public int executeIntegerScript(String source, String fileName) {
        return QuickJS._executeIntegerScript(this.getContextPtr(), source, fileName);
    }

    public double executeDoubleScript(String source, String fileName) {
        return QuickJS._executeDoubleScript(this.getContextPtr(), source, fileName);
    }

    public void executeVoidScript(String source, String fileName) {
        QuickJS._executeVoidScript(this.getContextPtr(), source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) {
        return QuickJS._executeBooleanScript(this.getContextPtr(), source, fileName);
    }

    public String executeStringScript(String source, String fileName) {
        // TODO
        return QuickJS._executeStringScript(this.getContextPtr(), source, fileName);
    }

    public Object executeScript(String source, String fileName) {
        // TODO
        return QuickJS._executeScript(this.getContextPtr(), source, fileName);
    }

    public JSArray executeArrayScript(String source, String fileName) {
        // TODO
//        long ptr = QuickJS._executeArrayScript(this.getContextPtr(), source, fileName);
//        return new JSArray(context, ptr);
        Object object = executeScript(source, fileName);
        if (object instanceof JSArray) {
            return (JSArray) object;
        }
        return null;

    }

    public JSObject executeObjectScript(String source, String fileName) {
        // TODO
//        long ptr = QuickJS._executeObjectScript(this.getContextPtr(), source, fileName);
//        return new JSObject(context, ptr);

        Object object = executeScript(source, fileName);
        if (object instanceof JSObject) {
            return (JSObject) object;
        }
        return null;
    }

    void registerCallback(JavaCallback callback, long objectHandle, String jsFunctionName) {
        long methodID = QuickJS._registerJavaMethod(this.getContextPtr(), objectHandle, jsFunctionName, false);
        MethodDescriptor methodDescriptor = new MethodDescriptor();
        methodDescriptor.callback = callback;
        this.functionRegistry.put(methodID, methodDescriptor);
    }


    void registerCallback(JavaVoidCallback callback, long objectHandle, String jsFunctionName) {
        long methodID = QuickJS._registerJavaMethod(this.getContextPtr(), objectHandle, jsFunctionName, true);
        MethodDescriptor methodDescriptor = new MethodDescriptor();
        methodDescriptor.voidCallback = callback;
        this.functionRegistry.put(methodID, methodDescriptor);
    }


    long initNewJSObject(long contextPtr) {
        return QuickJS._initNewJSObject(contextPtr);
    }

}
