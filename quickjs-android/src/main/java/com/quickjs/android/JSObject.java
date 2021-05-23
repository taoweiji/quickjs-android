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
        // TODO
        return null;
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
