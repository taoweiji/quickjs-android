package com.quickjs.android;

public class JSObject extends JSValue {

    public JSObject(JSContext context) {
        this(context, null);
    }

    JSObject() {

    }

    protected JSObject(JSContext context, Object data) {
        this.context = context;
        this.initialize(this.context.getContextPtr(), data);
    }

    public JSObject(JSContext context, long objectHandle) {
        this.context = context;
        this.objectHandle = objectHandle;
    }

    public JSObject set(String key, int value) {
        QuickJS._set(getContextPtr(), this.objectHandle, key, value);
        return this;
    }

    public JSObject set(String key, double value) {
        QuickJS._set(getContextPtr(), this.objectHandle, key, value);
        return this;
    }

    public JSObject set(String key, String value) {
        QuickJS._set(getContextPtr(), this.objectHandle, key, value);
        return this;
    }

    public JSObject set(String key, boolean value) {
        QuickJS._set(getContextPtr(), this.objectHandle, key, value);
        return this;
    }

    public JSObject set(String key, JSValue value) {
        QuickJS._setObject(getContextPtr(), this.objectHandle, key, value.objectHandle);
        return this;
    }

    public int getInteger(String key) {
        return QuickJS._getInteger(this.getContextPtr(), this.objectHandle, key);
    }

    public boolean getBoolean(String key) {
        return QuickJS._getBoolean(this.getContextPtr(), this.objectHandle, key);
    }

    public double getDouble(String key) {
        return QuickJS._getDouble(this.getContextPtr(), this.objectHandle, key);
    }

    public String getString(String key) {
        return QuickJS._getString(this.getContextPtr(), this.objectHandle, key);
    }

    public JSArray getArray(String key) {
        long ptr = QuickJS._getObject(this.getContextPtr(), this.objectHandle, key);
        JSArray jsArray = new JSArray();
        jsArray.context = context;
        jsArray.objectHandle = ptr;
        return jsArray;
    }

    public JSObject getObject(String key) {
        long ptr = QuickJS._getObject(this.getContextPtr(), this.objectHandle, key);
        JSObject jsObject = new JSObject();
        jsObject.context = context;
        jsObject.objectHandle = ptr;
        return jsObject;
    }


    public JSObject registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        context.registerCallback(callback, objectHandle, jsFunctionName);
        return this;
    }

    public JSObject registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
        context.registerCallback(callback, objectHandle, jsFunctionName);
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

    public Object executeJSFunction(String name) {
        return QuickJS.executeJSFunction(context, objectHandle, name, new Object[0]);
    }

    public Object executeJSFunction(String name, Object... parameters) {
        return QuickJS.executeJSFunction(context, objectHandle, name, parameters);
    }

    public boolean contains(String key) {
        return QuickJS._contains(getContextPtr(), this.objectHandle, key);
    }

    public String[] getKeys() {
        return QuickJS._getKeys(getContextPtr(), this.objectHandle);
    }


    Object executeFunction(int expectedType, String name, JSArray parameters) {
        long parametersHandle = parameters == null ? 0L : parameters.getHandle();
        return QuickJS.executeFunction(context, expectedType, objectHandle, name, parametersHandle);
    }
}
