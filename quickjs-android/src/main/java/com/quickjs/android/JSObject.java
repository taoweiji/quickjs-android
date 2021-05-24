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
        long parametersHandle = parameters == null ? 0L : parameters.getHandle();
        return QuickJS._executeFunction(getContextPtr(), 0, objectHandle, name, parametersHandle);
    }

    public int executeIntegerFunction(String name, JSArray parameters) {
        // TODO
        return 0;
    }

    public double executeDoubleFunction(String name, JSArray parameters) {
        // TODO
        return 0;
    }

    public String executeStringFunction(String name, JSArray parameters) {
        long parametersHandle = parameters == null ? 0L : parameters.getHandle();
        return (String) QuickJS._executeFunction(getContextPtr(), 0, objectHandle, name, parametersHandle);
    }

    public boolean executeBooleanFunction(String name, JSArray parameters) {
        // TODO
        return false;
    }

    public JSArray executeArrayFunction(String name, JSArray parameters) {
        // TODO
        return null;
    }

    public JSObject executeObjectFunction(String name, JSArray parameters) {
        // TODO
        return null;
    }

    public void executeVoidFunction(String name, JSArray parameters) {
        // TODO
    }


    public Object executeJSFunction(String name) {
        // TODO
        return null;
    }

    public Object executeJSFunction(String name, Object... parameters) {
        // TODO
        return null;
    }

    public boolean contains(String key) {
        // TODO
        return false;
    }

    public String[] getKeys() {
        // TODO
        return null;
    }


}
