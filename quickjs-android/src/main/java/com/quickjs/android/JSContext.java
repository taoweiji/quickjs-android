package com.quickjs.android;

public class JSContext {
    private long contextPtr;

    JSContext(long contextPtr) {
        this.contextPtr = contextPtr;
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

    public void close() {
        QuickJS._releaseContext(contextPtr);
    }
}
