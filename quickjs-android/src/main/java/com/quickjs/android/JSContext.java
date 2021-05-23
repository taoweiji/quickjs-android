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


    public void close() {
        QuickJS._releaseContext(contextPtr);
    }


    public long initNewJSObject(long contextPtr) {
        return QuickJS._initNewJSObject(contextPtr);
    }
}
