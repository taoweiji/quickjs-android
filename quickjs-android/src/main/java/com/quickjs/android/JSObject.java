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

    public void add(String key, int value) {
        QuickJS._add(getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, double value) {
        QuickJS._add(getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, String value) {
        QuickJS._add(getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, boolean value) {
        QuickJS._add(getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, JSValue value) {
        QuickJS._addObject(getContextPtr(), this.objectHandle, key, value.objectHandle);
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

//    public JSArray getArray(String key) {
//        return QuickJS._getInteger(this.getContextPtr(), this.objectHandle, key);
//    }
//
//    public JSObject getObject(String key) {
//        return QuickJS._getInteger(this.getContextPtr(), this.objectHandle, key);
//    }


    public JSObject registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        // TODO
        return null;
    }

    public JSObject registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
        // TODO
        return null;
    }

    public Object executeFunction(String name, JSArray parameters) {
        // TODO
        return null;
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
        return (String) QuickJS._executeFunction(getContextPtr(), 0, objectHandle, name, 0);
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
}
