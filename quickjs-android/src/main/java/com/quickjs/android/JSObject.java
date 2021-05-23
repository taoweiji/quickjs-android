package com.quickjs.android;

public class JSObject extends JSValue {

    public JSObject(JSContext context) {
        this(context, null);
    }

    protected JSObject(JSContext context, Object data) {
        this.context = context;
        this.initialize(this.context.getContextPtr(), data);
    }

    public void add(String key, int value) {
        QuickJS._add(context.getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, double value) {
        QuickJS._add(context.getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, String value) {
        QuickJS._add(context.getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, boolean value) {
        QuickJS._add(context.getContextPtr(), this.objectHandle, key, value);
    }

    public void add(String key, JSValue value) {
        QuickJS._addObject(context.getContextPtr(), this.objectHandle, key, value.objectHandle);
    }
}
