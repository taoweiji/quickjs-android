package com.quickjs.android;

public class JSContext extends JSObject {
    private final long contextPtr;

//    Map<Long, MethodDescriptor> functionRegistry = new HashMap<>();

    JSContext(long contextPtr) {
        super(null, QuickJS._getGlobalObject(contextPtr));
        this.contextPtr = contextPtr;
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
        QuickJS.functionRegistry.put(functionHandle.tag, methodDescriptor);
    }

    void registerCallback(JavaVoidCallback callback, JSFunction functionHandle) {
        QuickJS.MethodDescriptor methodDescriptor = new QuickJS.MethodDescriptor();
        methodDescriptor.voidCallback = callback;
        QuickJS.functionRegistry.put(functionHandle.tag, methodDescriptor);
    }

}
