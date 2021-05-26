package com.quickjs.android;

public class JSValue {
    public static final int NULL = 0;
    public static final int UNKNOWN = 0;
    public static final int INTEGER = 1;
    public static final int INT_32_ARRAY = 1;
    public static final int DOUBLE = 2;
    public static final int FLOAT_64_ARRAY = 2;
    public static final int BOOLEAN = 3;
    public static final int STRING = 4;
    public static final int JS_ARRAY = 5;
    public static final int JS_OBJECT = 6;
    public static final int JS_FUNCTION = 7;
    public static final int JS_TYPED_ARRAY = 8;
    public static final int BYTE = 9;
    public static final int INT_8_ARRAY = 9;
    public static final int JS_ARRAY_BUFFER = 10;
    public static final int UNSIGNED_INT_8_ARRAY = 11;
    public static final int UNSIGNED_INT_8_CLAMPED_ARRAY = 12;
    public static final int INT_16_ARRAY = 13;
    public static final int UNSIGNED_INT_16_ARRAY = 14;
    public static final int UNSIGNED_INT_32_ARRAY = 15;
    public static final int FLOAT_32_ARRAY = 16;
    public static final int UNDEFINED = 99;

    protected JSContext context;
    protected long objectHandle;
    protected boolean released;

    protected void initialize(long contextPtr, Object data) {
        long objectHandle = this.context.initNewJSObject(contextPtr);
        this.released = false;
        this.addObjectReference(objectHandle);
    }

    protected void addObjectReference(long objectHandle) {
        this.objectHandle = objectHandle;
        // TODO 考虑自动管理
    }

    long getContextPtr() {
        return context.getContextPtr();
    }

    long getHandle() {
        return this.objectHandle;
    }

    public void close() {
        QuickJS._release(getContextPtr(), this.objectHandle);
    }
    public static JSValue getNull(){
        return new JSValue();
    }


}
