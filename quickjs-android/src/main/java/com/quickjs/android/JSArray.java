package com.quickjs.android;

public class JSArray extends JSObject {

    JSArray() {

    }

    public JSArray(JSContext context) {
        this.context = context;
        this.initialize(this.context.getContextPtr(), null);
    }

    public JSArray(JSContext context, long objectHandle) {
        this.context = context;
        this.objectHandle = objectHandle;
    }


    @Override
    protected void initialize(long contextPtr, Object data) {
        long objectHandle = QuickJS._initNewJSArray(contextPtr);
        this.released = false;
        this.addObjectReference(objectHandle);
    }

    public int getInteger(int index) {
        return QuickJS._arrayGetInteger(this.getContextPtr(), this.objectHandle, index);
    }

    public boolean getBoolean(int index) {
        return QuickJS._arrayGetBoolean(this.getContextPtr(), this.objectHandle, index);
    }

    public double getDouble(int index) {
        return QuickJS._arrayGetDouble(this.getContextPtr(), this.objectHandle, index);
    }

    public String getString(int index) {
        return QuickJS._arrayGetString(this.getContextPtr(), this.objectHandle, index);
    }

    public JSArray push(int value) {
        QuickJS._arrayAdd(getContextPtr(), this.objectHandle, value);
        return this;
    }

    public JSArray push(double value) {
        QuickJS._arrayAdd(getContextPtr(), this.objectHandle, value);
        return this;
    }

    public JSArray push(String value) {
        QuickJS._arrayAdd(getContextPtr(), this.objectHandle, value);
        return this;
    }

    public JSArray push(boolean value) {
        QuickJS._arrayAdd(getContextPtr(), this.objectHandle, value);
        return this;
    }

    public JSArray push(JSValue value) {
        QuickJS._arrayAddObject(getContextPtr(), this.objectHandle, value.objectHandle);
        return this;
    }

    public int length() {
        return getInteger("length");
    }
}
