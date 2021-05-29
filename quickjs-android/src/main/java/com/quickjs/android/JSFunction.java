package com.quickjs.android;

public class JSFunction extends JSObject {
    public JSFunction(JSContext context, JavaCallback callback) {
        super(context, QuickJS._initNewJSFunction(context.getContextPtr(), false));
        this.context.registerCallback(callback, this);
    }

    public JSFunction(JSContext context, JavaVoidCallback callback) {
        super(context, QuickJS._initNewJSFunction(context.getContextPtr(), true));
        this.context.registerCallback(callback, this);
    }

    public JSFunction(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    public Object call(JSObject receiver, JSArray parameters) {
        if (receiver == null) {
            receiver = context;
        }
        return QuickJS._executeFunction2(context.getContextPtr(), JSValue.TYPE_UNKNOWN, receiver, this, parameters);
    }

    @Override
    public void close() {
        super.close();
        context.functionRegistry.remove(this.tag);
    }
}
