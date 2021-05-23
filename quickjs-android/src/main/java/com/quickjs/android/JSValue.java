package com.quickjs.android;

public class JSValue {
    protected JSContext context;
    protected long objectHandle;
    protected boolean released;

    protected void initialize(long contextPtr, Object data) {
        long objectHandle = this.context.initNewJSObject(contextPtr);
        this.released = false;
        this.addObjectReference(objectHandle);
    }

    private void addObjectReference(long objectHandle) {
        this.objectHandle = objectHandle;
        // TODO 考虑释放
    }

    long getContextPtr() {
        return context.getContextPtr();
    }
}
