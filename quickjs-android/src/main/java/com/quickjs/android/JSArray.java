package com.quickjs.android;

public class JSArray extends JSObject {

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
}
