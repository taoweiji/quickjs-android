package com.quickjs;

public class JSFunction extends JSObject {

    public JSFunction(JSContext context, JavaCallback callback) {
        super(context, context.getNative()._initNewJSFunction(context.getContextPtr(), callback.hashCode(), false));
        this.context._registerCallback(callback, this);
    }

    public JSFunction(JSContext context, JavaVoidCallback callback) {
        super(context, context.getNative()._initNewJSFunction(context.getContextPtr(), callback.hashCode(), true));
        this.context._registerCallback(callback, this);
    }

    JSFunction(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    public Object call(JSValue.TYPE type, JSObject receiver, JSArray parameters) {
        this.context.checkReleased();
        this.context.checkRuntime(parameters);
        if (receiver == null) {
            receiver = JSValue.Undefined(context);
        }
        Object result = getNative()._executeFunction2(context.getContextPtr(), type.value, receiver, this, parameters);
        QuickJS.checkException(context);
        return JSValue.checkType(result, type);
    }

    public Object call(JSObject receiver, JSArray parameters) {
        return call(TYPE.UNKNOWN, receiver, parameters);
    }

}
