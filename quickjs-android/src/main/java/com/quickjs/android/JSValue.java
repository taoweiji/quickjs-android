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
//    protected boolean released;

    long tag;
    int u_int32;
    double u_float64;
    long u_ptr;

    JSValue(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        this.context = context;
        this.tag = tag;
        this.u_int32 = u_int32;
        this.u_float64 = u_float64;
        this.u_ptr = u_ptr;
    }

    JSValue(JSContext context, JSValue value) {
        this.context = context;
        this.tag = value.tag;
        this.u_int32 = value.u_int32;
        this.u_float64 = value.u_float64;
        this.u_ptr = value.u_ptr;
    }

    long getContextPtr() {
        return context.getContextPtr();
    }

    public void close() {
        QuickJS._release(getContextPtr(), this);
    }

    public static JSValue getNull() {
        // TODO
        return null;
    }


}
