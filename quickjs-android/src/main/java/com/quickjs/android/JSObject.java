package com.quickjs.android;

public class JSObject extends JSValue {

    public JSObject(JSContext context) {
        super(context, QuickJS._initNewJSObject(context.getContextPtr()));
    }

    public JSObject(JSContext context, JSValue value) {
        super(context, value);
    }

    JSObject(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

//    protected JSObject(JSContext context, Object data) {
//        this.context = context;
//        this.initialize(this.context.getContextPtr(), data);
//    }


    public JSObject set(String key, int value) {
        QuickJS._set(getContextPtr(), this, key, value);
        return this;
    }

    public JSObject set(String key, double value) {
        QuickJS._set(getContextPtr(), this, key, value);
        return this;
    }

    public JSObject set(String key, String value) {
        QuickJS._set(getContextPtr(), this, key, value);
        return this;
    }

    public JSObject set(String key, boolean value) {
        QuickJS._set(getContextPtr(), this, key, value);
        return this;
    }

    public JSObject set(String key, JSValue value) {
        QuickJS._set(getContextPtr(), this, key, value);
        return this;
    }

    public int getInteger(String key) {
        return QuickJS._getInteger(this.getContextPtr(), this, key);
    }

    public boolean getBoolean(String key) {
        return QuickJS._getBoolean(this.getContextPtr(), this, key);
    }

    public double getDouble(String key) {
        return QuickJS._getDouble(this.getContextPtr(), this, key);
    }

    public String getString(String key) {
        return QuickJS._getString(this.getContextPtr(), this, key);
    }

    public JSArray getArray(String key) {
        JSValue value = getObject(key);
        if (value instanceof JSArray) {
            return (JSArray) value;
        }
        return null;
    }

    public JSObject getObject(String key) {
        JSValue value = QuickJS._getObject(this.getContextPtr(), this, key);
        if (value instanceof JSObject) {
            return (JSObject) value;
        }
        return null;
    }

    public int getType(String key) {
        // TODO
//        long ptr = QuickJS._getObject(this.getContextPtr(), this, key);
        return QuickJS._getObjectType(this.getContextPtr(), this);
    }


    public JSObject registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        context.registerCallback(callback, this, jsFunctionName);
        return this;
    }

    public JSObject registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
        context.registerCallback(callback, this, jsFunctionName);
        return this;
    }


    public Object executeFunction(String name, JSArray parameters) {
        return executeFunction(JSValue.UNKNOWN, name, parameters);
    }

    public int executeIntegerFunction(String name, JSArray parameters) {
        return (int) executeFunction(JSValue.INTEGER, name, parameters);
    }

    public double executeDoubleFunction(String name, JSArray parameters) {
        return (double) executeFunction(JSValue.DOUBLE, name, parameters);
    }

    public boolean executeBooleanFunction(String name, JSArray parameters) {
        return (boolean) executeFunction(JSValue.BOOLEAN, name, parameters);
    }

    public String executeStringFunction(String name, JSArray parameters) {
        return (String) executeFunction(JSValue.STRING, name, parameters);
    }

    public JSArray executeArrayFunction(String name, JSArray parameters) {
        return (JSArray) executeFunction(JSValue.JS_ARRAY, name, parameters);
    }

    public JSObject executeObjectFunction(String name, JSArray parameters) {
        return (JSObject) executeFunction(JSValue.JS_OBJECT, name, parameters);
    }

    public void executeVoidFunction(String name, JSArray parameters) {
        executeFunction(JSValue.NULL, name, parameters);
    }

    public Object executeFunction2(String name, Object... parameters) {
        return QuickJS.executeJSFunction(context, this, name, parameters);
    }

    public boolean contains(String key) {
        return QuickJS._contains(getContextPtr(), this, key);
    }

    public String[] getKeys() {
        return QuickJS._getKeys(getContextPtr(), this);
    }


    Object executeFunction(int expectedType, String name, JSArray parameters) {
        return QuickJS.executeFunction(context, expectedType, this, name, parameters);
    }
}
