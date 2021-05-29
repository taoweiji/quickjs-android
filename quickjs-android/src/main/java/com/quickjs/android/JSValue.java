package com.quickjs.android;

public class JSValue {
    static final int TYPE_NULL = 0;
    static final int TYPE_UNKNOWN = 0;
    static final int TYPE_INTEGER = 1;
    static final int TYPE_INT_32_ARRAY = 1;
    static final int TYPE_DOUBLE = 2;
    static final int TYPE_FLOAT_64_ARRAY = 2;
    static final int TYPE_BOOLEAN = 3;
    static final int TYPE_STRING = 4;
    static final int TYPE_JS_ARRAY = 5;
    static final int TYPE_JS_OBJECT = 6;
    static final int TYPE_JS_FUNCTION = 7;
    static final int TYPE_JS_TYPED_ARRAY = 8;
    static final int TYPE_BYTE = 9;
    static final int TYPE_INT_8_ARRAY = 9;
    static final int TYPE_JS_ARRAY_BUFFER = 10;
    static final int TYPE_UNSIGNED_INT_8_ARRAY = 11;
    static final int TYPE_UNSIGNED_INT_8_CLAMPED_ARRAY = 12;
    static final int TYPE_INT_16_ARRAY = 13;
    static final int TYPE_UNSIGNED_INT_16_ARRAY = 14;
    static final int TYPE_UNSIGNED_INT_32_ARRAY = 15;
    static final int TYPE_FLOAT_32_ARRAY = 16;
    static final int TYPE_UNDEFINED = 99;

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

    public static class Undefined extends JSObject {
        public Undefined(JSContext context) {
            super(context);
        }
    }

    public static class NULL extends JSObject {
        public NULL(JSContext context) {
            super(context);
        }
    }

}
