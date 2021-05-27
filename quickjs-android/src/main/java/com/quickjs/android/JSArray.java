package com.quickjs.android;

public class JSArray extends JSObject {


    public JSArray(JSContext context) {
        super(context, QuickJS._initNewJSArray(context.getContextPtr()));
    }

    JSArray(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    public int getInteger(int index) {
        return QuickJS._arrayGetInteger(this.getContextPtr(), this, index);
    }

    public boolean getBoolean(int index) {
        return QuickJS._arrayGetBoolean(this.getContextPtr(), this, index);
    }

    public double getDouble(int index) {
        return QuickJS._arrayGetDouble(this.getContextPtr(), this, index);
    }

    public String getString(int index) {
        return QuickJS._arrayGetString(this.getContextPtr(), this, index);
    }

    public JSObject getObject(int index) {
        return QuickJS._arrayGetObject(this.getContextPtr(), this, index);
    }

    public JSArray getArray(int index) {
        return QuickJS._arrayGetArray(this.getContextPtr(), this, index);
    }

    public JSArray push(int value) {
        QuickJS._arrayAdd(getContextPtr(), this, value);
        return this;
    }

    public JSArray push(double value) {
        QuickJS._arrayAdd(getContextPtr(), this, value);
        return this;
    }

    public JSArray push(String value) {
        QuickJS._arrayAdd(getContextPtr(), this, value);
        return this;
    }

    public JSArray push(boolean value) {
        QuickJS._arrayAdd(getContextPtr(), this, value);
        return this;
    }

    public JSArray push(JSValue value) {
        QuickJS._arrayAdd(getContextPtr(), this, value);
        return this;
    }

    public int length() {
        return getInteger("length");
    }
}
