package com.quickjs;

import androidx.annotation.Keep;

import java.util.Arrays;

@Keep
public class JSValue {
//    JS_TAG_FIRST       = -11, /* first negative tag */
//    JS_TAG_BIG_DECIMAL = -11,
//    JS_TAG_BIG_INT     = -10,
//    JS_TAG_BIG_FLOAT   = -9,
//    JS_TAG_SYMBOL      = -8,
//    JS_TAG_STRING      = -7,
//    JS_TAG_MODULE      = -3, /* used internally */
//    JS_TAG_FUNCTION_BYTECODE = -2, /* used internally */
//    JS_TAG_OBJECT      = -1,
//
//    JS_TAG_INT         = 0,
//    JS_TAG_BOOL        = 1,
//    JS_TAG_NULL        = 2,
//    JS_TAG_UNDEFINED   = 3,
//    JS_TAG_UNINITIALIZED = 4,
//    JS_TAG_CATCH_OFFSET = 5,
//    JS_TAG_EXCEPTION   = 6,
//    JS_TAG_FLOAT64     = 7,


    static final int TYPE_NULL = 0;
    static final int TYPE_UNKNOWN = 0;
    static final int TYPE_INTEGER = 1;
    static final int TYPE_DOUBLE = 2;
    static final int TYPE_BOOLEAN = 3;
    static final int TYPE_STRING = 4;
    static final int TYPE_JS_ARRAY = 5;
    static final int TYPE_JS_OBJECT = 6;
    static final int TYPE_JS_FUNCTION = 7;
    static final int TYPE_INT_32_ARRAY = 1;
    static final int TYPE_FLOAT_64_ARRAY = 2;
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

    long tag;
    int u_int32;
    double u_float64;
    long u_ptr;

    volatile boolean released = false;

    protected static Object checkType(Object result, TYPE type) {
        switch (type.value) {
            case TYPE_UNKNOWN:
                return result;
            case TYPE_INTEGER:
                if (result instanceof Integer) {
                    return result;
                }
                return 0;
            case TYPE_DOUBLE:
                if (result instanceof Double) {
                    return result;
                }
                return 0;
            case TYPE_BOOLEAN:
                if (result instanceof Boolean) {
                    return result;
                }
                return false;
            case TYPE_STRING:
                if (result instanceof String) {
                    return result;
                }
                return null;
            case TYPE_JS_ARRAY:
                if (result instanceof JSArray) {
                    return result;
                }
                return null;
            case TYPE_JS_FUNCTION:
                if (result instanceof JSFunction) {
                    return result;
                }
                return null;
            case TYPE_JS_OBJECT:
                if (result instanceof JSObject) {
                    return result;
                }
                return null;
        }
        return null;
    }

    public long getTag() {
        return tag;
    }

    public enum TYPE {
        NULL(TYPE_NULL),
        UNKNOWN(TYPE_UNKNOWN),
        UNDEFINED(TYPE_UNDEFINED),
        INTEGER(TYPE_INTEGER),
        DOUBLE(TYPE_DOUBLE),
        BOOLEAN(TYPE_BOOLEAN),
        STRING(TYPE_STRING),
        JS_ARRAY(TYPE_JS_ARRAY),
        JS_OBJECT(TYPE_JS_OBJECT),
        JS_FUNCTION(TYPE_JS_FUNCTION);

        final int value;

        TYPE(int value) {
            this.value = value;
        }
    }

    JSValue(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        this.context = context;
        this.tag = tag;
        this.u_int32 = u_int32;
        this.u_float64 = u_float64;
        this.u_ptr = u_ptr;
        if (context != null) {
            context.addObjRef(this);
        }
    }

    JSValue(JSContext context, JSValue value) {
        // 赋值给新的对象，原来的对象就要标记成销毁
        value.released = true;
        this.context = context;
        this.tag = value.tag;
        this.u_int32 = value.u_int32;
        this.u_float64 = value.u_float64;
        this.u_ptr = value.u_ptr;
        if (context != null) {
            value.released = true;
            context.removeObjRef(value);
            context.addObjRef(this);
        }
        if (context != null) {
            context.checkReleased();
        }
    }

    long getContextPtr() {
        return context.getContextPtr();
    }

    protected void close() {
        close(false);
    }

    private void close(boolean finalize) {
        if (released) {
            return;
        }
        released = true;
        context.releaseObjRef(this, finalize);
    }

    public boolean isUndefined() {
        return getContext().getNative()._isUndefined(getContextPtr(), this);
    }

    public TYPE getType() {
        this.context.checkReleased();
        int value = getContext().getNative()._getObjectType(getContextPtr(), this);
        switch (value) {
            case TYPE_UNDEFINED:
                return TYPE.UNDEFINED;
            case TYPE_UNKNOWN:
                return TYPE.UNKNOWN;
            case TYPE_INTEGER:
                return TYPE.INTEGER;
            case TYPE_DOUBLE:
                return TYPE.DOUBLE;
            case TYPE_BOOLEAN:
                return TYPE.BOOLEAN;
            case TYPE_STRING:
                return TYPE.STRING;
            case TYPE_JS_ARRAY:
                return TYPE.JS_ARRAY;
            case TYPE_JS_FUNCTION:
                return TYPE.JS_FUNCTION;
            case TYPE_JS_OBJECT:
                return TYPE.JS_OBJECT;
        }
        return TYPE.UNKNOWN;
    }

    public static JSObject Undefined(JSContext context) {
        return (JSObject) context.getNative()._Undefined(context.getContextPtr());
    }

    public static JSValue NULL() {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSValue jsValue = (JSValue) o;
        return jsValue.tag == this.tag;
    }

    @Override
    protected void finalize() throws Throwable {
        if (!(this instanceof JSContext)) {
            close(true);
        }
        super.finalize();
    }

    public JSContext getContext() {
        return context;
    }

    protected QuickJSNative getNative() {
        return getContext().getNative();
    }

    public QuickJS getQuickJS() {
        return getContext().getQuickJS();
    }

    public void postEventQueue(Runnable event) {
        getQuickJS().postEventQueue(event);
    }
}
