package com.quickjs;

import android.webkit.JavascriptInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class JSObject extends JSValue {

    public JSObject(JSContext context) {
        super(context, QuickJS._initNewJSObject(context.getContextPtr()));
    }

    JSObject(JSContext context, JSValue value) {
        super(context, value);
    }

    JSObject(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    protected JSObject setObject(String key, Object value) {
        QuickJS._set(getContextPtr(), this, key, value);
        return this;
    }

    protected Object get(TYPE expectedType, String key) {
        Object object = QuickJS._get(this.getContextPtr(), expectedType.value, this, key);
        return JSValue.checkType(object, expectedType);
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
        return (int) get(JSValue.TYPE.INTEGER, key);
    }

    public boolean getBoolean(String key) {
        return (boolean) get(JSValue.TYPE.BOOLEAN, key);
    }

    public double getDouble(String key) {
        return (double) get(JSValue.TYPE.DOUBLE, key);
    }

    public String getString(String key) {
        return (String) get(JSValue.TYPE.STRING, key);
    }

    public JSArray getArray(String key) {
        return (JSArray) get(JSValue.TYPE.JS_ARRAY, key);
    }

    public JSObject getObject(String key) {
        return (JSObject) get(JSValue.TYPE.JS_OBJECT, key);
    }


    public TYPE getType(String key) {
        JSValue value = QuickJS._getValue(this.getContextPtr(), this, key);
        if (value == null) {
            return TYPE.NULL;
        }
        return value.getType();
    }


    public JSObject registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        JSFunction functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), this, jsFunctionName, callback.hashCode(), false);
        context.registerCallback(callback, functionHandle);
        return this;
    }

    public JSObject registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
        JSFunction functionHandle = QuickJS._registerJavaMethod(this.getContextPtr(), this, jsFunctionName, callback.hashCode(), true);
        context.registerCallback(callback, functionHandle);
        return this;
    }

    public Object executeFunction(String name, JSArray parameters) {
        return executeFunction(JSValue.TYPE.UNKNOWN, name, parameters);
    }

    public int executeIntegerFunction(String name, JSArray parameters) {
        return (int) executeFunction(JSValue.TYPE.INTEGER, name, parameters);
    }

    public double executeDoubleFunction(String name, JSArray parameters) {
        return (double) executeFunction(JSValue.TYPE.DOUBLE, name, parameters);
    }

    public boolean executeBooleanFunction(String name, JSArray parameters) {
        return (boolean) executeFunction(JSValue.TYPE.BOOLEAN, name, parameters);
    }

    public String executeStringFunction(String name, JSArray parameters) {
        return (String) executeFunction(JSValue.TYPE.STRING, name, parameters);
    }

    public JSArray executeArrayFunction(String name, JSArray parameters) {
        return (JSArray) executeFunction(JSValue.TYPE.JS_ARRAY, name, parameters);
    }

    public JSObject executeObjectFunction(String name, JSArray parameters) {
        return (JSObject) executeFunction(JSValue.TYPE.JS_OBJECT, name, parameters);
    }

    public void executeVoidFunction(String name, JSArray parameters) {
        executeFunction(JSValue.TYPE.NULL, name, parameters);
    }

    public Object executeFunction2(String name, Object... parameters) {
        return QuickJS.executeFunction(context, this, name, parameters);
    }

    public boolean contains(String key) {
        return QuickJS._contains(getContextPtr(), this, key);
    }

    public String[] getKeys() {
        return QuickJS._getKeys(getContextPtr(), this);
    }


    protected Object executeFunction(TYPE expectedType, String name, JSArray parameters) {
        Object object = QuickJS._executeFunction(context.getContextPtr(), expectedType.value, this, name, parameters);
        return JSValue.checkType(object, expectedType);
    }

    public void addJavascriptInterface(Object obj, String interfaceName) {
        Method[] methods = obj.getClass().getMethods();
        JSObject object = new JSObject(context);
        for (Method method : methods) {
            if (method.getAnnotation(JavascriptInterface.class) == null) {
                continue;
            }
            String functionName = method.getName();
            if (method.getReturnType().equals(Void.TYPE)) {
                object.registerJavaMethod((receiver, args) -> {
                    try {
                        method.invoke(obj, getParameters(method, args));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }, functionName);
            } else {
                object.registerJavaMethod((receiver, args) -> {
                    try {
                        return method.invoke(obj, getParameters(method, args));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }, functionName);
            }
        }
        set(interfaceName, object);
    }

    private Object[] getParameters(Method method, JSArray args) {
        Object[] objects = new Object[args.length()];
        Type[] types = method.getGenericParameterTypes();
        for (int i = 0; i < objects.length; i++) {
            Type type = types[i];
            if (type == int.class || type == Integer.class) {
                objects[i] = args.getInteger(i);
            } else if (type == double.class || type == Double.class) {
                objects[i] = args.getDouble(i);
            } else if (type == boolean.class || type == Boolean.class) {
                objects[i] = args.getBoolean(i);
            } else if (type == String.class) {
                objects[i] = args.getString(i);
            } else if (type == JSArray.class) {
                objects[i] = args.getArray(i);
            } else if (type == JSObject.class || type == JSFunction.class) {
                objects[i] = args.getObject(i);
            } else {
                throw new RuntimeException("Type error");
            }
        }
        return objects;
    }

    static class Undefined extends JSObject {

        Undefined(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
            super(context, tag, u_int32, u_float64, u_ptr);
            released = true;
        }

        @Override
        protected JSObject setObject(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Object get(TYPE expectedType, String key) {
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
        protected Object executeFunction(TYPE expectedType, String name, JSArray parameters) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return JSValue.TYPE_UNDEFINED;
        }

    }
}
