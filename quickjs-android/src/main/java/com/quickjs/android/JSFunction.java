package com.quickjs.android;

public class JSFunction extends JSObject {
    public JSFunction(JSContext context, JavaCallback callback) {
        this.context = context;
        this.objectHandle = QuickJS._initNewJSFunction(context.getContextPtr(), false);
        this.context.registerCallback(callback, this.objectHandle);
    }

    public JSFunction(JSContext context, JavaVoidCallback callback) {
        this.context = context;
        this.objectHandle = QuickJS._initNewJSFunction(context.getContextPtr(), true);
        this.context.registerCallback(callback, this.objectHandle);
    }


    public JSFunction(JSContext context, long objectHandle) {
        this.context = context;
        this.objectHandle = objectHandle;
    }

    public Object call(JSObject receiver, JSArray parameters) {
        return QuickJS.executeFunction(context, JSValue.UNKNOWN, receiver.objectHandle, objectHandle, parameters.objectHandle);
    }
}
