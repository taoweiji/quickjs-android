package com.quickjs.android;

public class JSObject extends JSValue {

    public JSObject(JSContext context) {
        super(context, QuickJS._initNewJSObject(context.getContextPtr()));
    }

    public JSObject(JSContext context, JSValue value) {
        super(context, value);
    }

    JSObject(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    JSObject setObject(String key, Object value) {
        QuickJS._set(getContextPtr(), this, key, value);
        return this;
    }

    Object get(int expectedType, String key) {
        return QuickJS._get(this.getContextPtr(), expectedType, this, key);
    }


    public JSObject set(String key, int value) {
        return setObject(key, value);
    }

    public JSObject set(String key, double value) {
        return setObject(key, value);
    }

    public JSObject set(String key, String value) {
        return setObject(key, value);
    }

    public JSObject set(String key, boolean value) {
        return setObject(key, value);
    }

    public JSObject set(String key, JSValue value) {
        return setObject(key, value);
    }

    public int getInteger(String key) {
        Object result = get(JSValue.TYPE_INTEGER, key);
        if (result instanceof Integer) {
            return (int) result;
        }
        return 0;
    }

    public boolean getBoolean(String key) {
        Object result = get(JSValue.TYPE_BOOLEAN, key);
        if (result instanceof Boolean) {
            return (boolean) result;
        }
        return false;
    }

    public double getDouble(String key) {
        Object result = get(JSValue.TYPE_DOUBLE, key);
        if (result instanceof Double) {
            return (double) result;
        }
        return 0;
    }

    public String getString(String key) {
        Object result = get(JSValue.TYPE_STRING, key);
        if (result instanceof String) {
            return (String) result;
        }
        return null;
    }

    public JSArray getArray(String key) {
        Object value = get(JSValue.TYPE_JS_ARRAY, key);
        if (value instanceof JSArray) {
            return (JSArray) value;
        }
        return null;
    }

    public JSObject getObject(String key) {
        Object value = get(JSValue.TYPE_JS_OBJECT, key);
        if (value instanceof JSObject) {
            return (JSObject) value;
        }
        return null;
    }


    public int getType(String key) {
        JSValue value = QuickJS._getValue(this.getContextPtr(), this, key);
        if (value == null) {
            return JSValue.TYPE_NULL;
        }
        return value.getJSType();
    }


    public JSObject registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        JSFunction functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), this, jsFunctionName, false);
        context.registerCallback(callback, functionHandle);
        return this;
    }

    public JSObject registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
        JSFunction functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), this, jsFunctionName, true);
        context.registerCallback(callback, functionHandle);
        return this;
    }


    public Object executeFunction(String name, JSArray parameters) {
        return executeFunction(JSValue.TYPE_UNKNOWN, name, parameters);
    }

    public int executeIntegerFunction(String name, JSArray parameters) {
        Object result = executeFunction(JSValue.TYPE_INTEGER, name, parameters);
        if (result instanceof Integer) {
            return (int) result;
        }
        return 0;
    }

    public double executeDoubleFunction(String name, JSArray parameters) {
        Object result = executeFunction(JSValue.TYPE_DOUBLE, name, parameters);
        if (result instanceof Double) {
            return (double) result;
        }
        return 0;
    }

    public boolean executeBooleanFunction(String name, JSArray parameters) {
        Object result = executeFunction(JSValue.TYPE_BOOLEAN, name, parameters);
        if (result instanceof Boolean) {
            return (boolean) result;
        }
        return false;
    }

    public String executeStringFunction(String name, JSArray parameters) {
        Object result = executeFunction(JSValue.TYPE_STRING, name, parameters);
        if (result instanceof String) {
            return (String) result;
        }
        return null;
    }

    public JSArray executeArrayFunction(String name, JSArray parameters) {
        Object result = executeFunction(JSValue.TYPE_JS_ARRAY, name, parameters);
        if (result instanceof JSArray) {
            return (JSArray) result;
        }
        return null;
    }

    public JSObject executeObjectFunction(String name, JSArray parameters) {
        Object result = executeFunction(JSValue.TYPE_JS_OBJECT, name, parameters);
        if (result instanceof JSObject) {
            return (JSObject) result;
        }
        return null;
    }

    public void executeVoidFunction(String name, JSArray parameters) {
        executeFunction(JSValue.TYPE_NULL, name, parameters);
    }

    public Object executeFunction2(String name, Object... parameters) {
        return QuickJS.executeJSFunction(context, this, name, parameters);
    }

    public boolean contains(String key) {
        return QuickJS._contains(getContextPtr(), this, key);
    }

    public String[] getKeys() {
        return QuickJS._getKeys(getContextPtr(), this);
    }


    protected Object executeFunction(int expectedType, String name, JSArray parameters) {
        return QuickJS._executeFunction(context.getContextPtr(), expectedType, this, name, parameters);
    }

    static class Undefined extends JSObject {

        Undefined(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
            super(context, tag, u_int32, u_float64, u_ptr);
            released = true;
        }

        @Override
        JSObject setObject(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        Object get(int expectedType, String key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JSObject registerJavaMethod(JavaCallback callback, String jsFunctionName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JSObject registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Object executeFunction(int expectedType, String name, JSArray parameters) {
            throw new UnsupportedOperationException();
        }
    }
}
