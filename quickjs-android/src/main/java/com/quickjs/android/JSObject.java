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


    public int executeIntegerScript(String source, String fileName) {
        return QuickJS._executeIntegerScript(this.getContextPtr(), source, fileName);
    }

    public double executeDoubleScript(String source, String fileName) {
        return QuickJS._executeDoubleScript(this.getContextPtr(), source, fileName);
    }

    public String executeStringScript(String source, String fileName) {
        return QuickJS._executeStringScript(this.getContextPtr(), source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) {
        return QuickJS._executeBooleanScript(this.getContextPtr(), source, fileName);
    }

    public Object executeScript(String source, String fileName) {
        return QuickJS._executeScript(this.getContextPtr(), source, fileName);
    }

    public void executeVoidScript(String source, String fileName) {
        QuickJS._executeVoidScript(this.getContextPtr(), source, fileName);
    }

    public void executeArrayScript(String source, String fileName) {
        // TODO
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
