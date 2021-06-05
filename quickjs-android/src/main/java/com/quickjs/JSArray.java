package com.quickjs;

public class JSArray extends JSObject {

    public JSArray(JSContext context) {
        super(context, QuickJS._initNewJSArray(context.getContextPtr()));
    }

    JSArray(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    public Object get(TYPE expectedType, int index) {
        this.context.checkReleased();
        if (expectedType == null) {
            expectedType = TYPE.UNKNOWN;
        }
        Object object = QuickJS._arrayGet(this.getContextPtr(), expectedType.value, this, index);
        return JSValue.checkType(object, expectedType);
    }

    JSArray pushObject(Object value) {
        this.context.checkReleased();
        QuickJS._arrayAdd(getContextPtr(), this, value);
        return this;
    }

    public int getInteger(int index) {
        Object result = get(JSValue.TYPE.INTEGER, index);
        if (result instanceof Integer) {
            return (int) result;
        }
        return 0;
    }

    public boolean getBoolean(int index) {
        Object result = get(JSValue.TYPE.BOOLEAN, index);
        if (result instanceof Boolean) {
            return (boolean) result;
        }
        return false;
    }

    public double getDouble(int index) {
        Object result = get(JSValue.TYPE.DOUBLE, index);
        if (result instanceof Double) {
            return (double) result;
        }
        return 0;
    }

    public String getString(int index) {
        Object result = get(JSValue.TYPE.STRING, index);
        if (result instanceof String) {
            return (String) result;
        }
        return null;
    }

    public JSObject getObject(int index) {
        Object result = get(JSValue.TYPE.JS_OBJECT, index);
        if (result instanceof JSObject) {
            return (JSObject) result;
        }
        return null;
    }

    public JSArray getArray(int index) {
        Object result = get(JSValue.TYPE.JS_ARRAY, index);
        if (result instanceof JSArray) {
            return (JSArray) result;
        }
        return null;
    }

    public TYPE getType(int index) {
        this.context.checkReleased();
        JSValue value = QuickJS._arrayGetValue(this.getContextPtr(), this, index);
        if (value == null) {
            return JSValue.TYPE.NULL;
        }
        return value.getType();
    }

    public JSArray push(int value) {
        return pushObject(value);
    }

    public JSArray push(double value) {
        return pushObject(value);
    }

    public JSArray push(String value) {
        return pushObject(value);
    }

    public JSArray push(boolean value) {
        return pushObject(value);
    }

    public JSArray push(JSValue value) {
        this.context.checkRuntime(value);
        return pushObject(value);
    }

    public int length() {
        return getInteger("length");
    }
}
