package com.quickjs.android;

public class JSContext extends JSObject {
    private final long contextPtr;

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

    public String executeStringScript(String source, String fileName) {
        return QuickJS._executeStringScript(this.getContextPtr(), source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) {
        return QuickJS._executeBooleanScript(this.getContextPtr(), source, fileName);
    }

    public Object executeScript(String source, String fileName) {
        return QuickJS._executeScript(this.getContextPtr(), source, fileName);
    }

    public void executeVoidScript(String source, String fileName) {
        QuickJS._executeVoidScript(this.getContextPtr(), source, fileName);
    }

    public void executeArrayScript(String source, String fileName) {
        // TODO
    }


    public long initNewJSObject(long contextPtr) {
        return QuickJS._initNewJSObject(contextPtr);
    }
}
