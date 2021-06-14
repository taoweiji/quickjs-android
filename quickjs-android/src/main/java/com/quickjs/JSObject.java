package com.quickjs;

import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;

public class JSObject extends JSValue {

    public JSObject(JSContext context) {
        super(context, context.getNative()._initNewJSObject(context.getContextPtr()));
    }

    public JSObject(JSContext context, JSONObject jsonObject) {
        this(context, context.getNative()._initNewJSObject(context.getContextPtr()));
        append(this, jsonObject);
    }

    JSObject(JSContext context, JSValue value) {
        super(context, value);
    }

    JSObject(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    public static void append(JSObject jsObject, JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        Iterator<String> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = it.next();
            Object obj = jsonObject.opt(key);
            if (obj instanceof String) {
                jsObject.set(key, (String) obj);
            } else if (obj instanceof Integer) {
                jsObject.set(key, (Integer) obj);
            } else if (obj instanceof Boolean) {
                jsObject.set(key, (Boolean) obj);
            } else if (obj instanceof Number) {
                jsObject.set(key, ((Number) obj).doubleValue());
            } else if (obj instanceof JSONObject) {
                jsObject.set(key, new JSObject(jsObject.context, (JSONObject) obj));
            } else if (obj instanceof JSONArray) {
                jsObject.set(key, new JSArray(jsObject.context, (JSONArray) obj));
            }
        }
    }

    protected JSObject setObject(String key, Object value) {
        this.context.checkReleased();
        context.getNative()._set(getContextPtr(), this, key, value);
        return this;
    }

    public Object get(String key) {
        return get(TYPE.UNKNOWN, key);
    }

    Object get(TYPE expectedType, String key) {
        this.context.checkReleased();
        if (expectedType == null) {
            expectedType = TYPE.UNKNOWN;
        }
        Object object = context.getNative()._get(this.getContextPtr(), expectedType.value, this, key);
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
        this.context.checkRuntime(value);
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
        JSValue value = getContext().getNative()._getValue(this.getContextPtr(), this, key);
        if (value == null) {
            return TYPE.NULL;
        }
        return value.getType();
    }


    public JSFunction registerJavaMethod(JavaCallback callback, String jsFunctionName) {
        this.context.checkReleased();
        JSFunction functionHandle = getNative()._registerJavaMethod(this.getContextPtr(), this, jsFunctionName, callback.hashCode(), false);
        context._registerCallback(callback, functionHandle);
        return functionHandle;
    }

    public JSFunction registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
        this.context.checkReleased();
        JSFunction functionHandle = getNative()._registerJavaMethod(this.getContextPtr(), this, jsFunctionName, callback.hashCode(), true);
        context._registerCallback(callback, functionHandle);
        return functionHandle;
    }

    public JSFunction registerClass(JavaConstructorCallback javaConstructorCallback, String className) {
        JavaCallback callback = (receiver, args) -> {
            JSObject thisObj = new JSObject(receiver.context);
            javaConstructorCallback.invoke(thisObj, args);
            return thisObj;
        };
        JSFunction functionHandle = context.getNative()._newClass(context.getContextPtr(), callback.hashCode());
        this.context._registerCallback(callback, functionHandle);
        this.set(className, functionHandle);
        return functionHandle;
    }

    public Object executeFunction(String name, JSArray parameters) {
        return _executeFunction(JSValue.TYPE.UNKNOWN, name, parameters);
    }

    public int executeIntegerFunction(String name, JSArray parameters) {
        return (int) _executeFunction(JSValue.TYPE.INTEGER, name, parameters);
    }

    public double executeDoubleFunction(String name, JSArray parameters) {
        return (double) _executeFunction(JSValue.TYPE.DOUBLE, name, parameters);
    }

    public boolean executeBooleanFunction(String name, JSArray parameters) {
        return (boolean) _executeFunction(JSValue.TYPE.BOOLEAN, name, parameters);
    }

    public String executeStringFunction(String name, JSArray parameters) {
        return (String) _executeFunction(JSValue.TYPE.STRING, name, parameters);
    }

    public JSArray executeArrayFunction(String name, JSArray parameters) {
        return (JSArray) _executeFunction(JSValue.TYPE.JS_ARRAY, name, parameters);
    }

    public JSObject executeObjectFunction(String name, JSArray parameters) {
        return (JSObject) _executeFunction(JSValue.TYPE.JS_OBJECT, name, parameters);
    }

    public void executeVoidFunction(String name, JSArray parameters) {
        _executeFunction(JSValue.TYPE.NULL, name, parameters);
    }

    public Object executeFunction2(String name, Object... parameters) {
        this.context.checkReleased();
        return QuickJS.executeFunction(context, this, name, parameters);
    }

    public boolean contains(String key) {
        this.context.checkReleased();
        return getContext().getNative()._contains(getContextPtr(), this, key);
    }

    public String[] getKeys() {
        this.context.checkReleased();
        return getContext().getNative()._getKeys(getContextPtr(), this);
    }


    protected Object _executeFunction(TYPE expectedType, String name, JSArray parameters) {
        this.context.checkReleased();
        this.context.checkRuntime(parameters);
        Object object = getNative()._executeFunction(context.getContextPtr(), expectedType.value, this, name, parameters);
        QuickJS.checkException(context);
        return JSValue.checkType(object, expectedType);
    }

    public void appendJavascriptInterface(Object obj) {
        appendJavascriptInterface(this, obj);
    }

    public JSObject addJavascriptInterface(Object obj, String interfaceName) {
        this.context.checkReleased();
        JSObject jsObject = new JSObject(context);
        appendJavascriptInterface(jsObject, obj);
        set(interfaceName, jsObject);
        return jsObject;
    }

    protected static void appendJavascriptInterface(JSObject jsObject, Object obj) {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(JavascriptInterface.class) == null) {
                continue;
            }
            String functionName = method.getName();
            if (method.getReturnType().equals(Void.TYPE)) {
                jsObject.registerJavaMethod((receiver, args) -> {
                    try {
                        method.invoke(obj, getParameters(method, args));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }, functionName);
            } else {
                jsObject.registerJavaMethod((receiver, args) -> {
                    try {
                        return method.invoke(obj, getParameters(method, args));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }, functionName);
            }
        }
    }

    private static Object[] getParameters(Method method, JSArray args) {
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

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        String[] keys = getKeys();
        for (String key : keys) {
            Object obj = this.get(key);
            if (obj instanceof Undefined || obj instanceof JSFunction) {
                continue;
            }
            if (obj instanceof Number || obj instanceof String || obj instanceof Boolean) {
                try {
                    jsonObject.put(key, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (obj instanceof JSArray) {
                try {
                    jsonObject.put(key, ((JSArray) obj).toJSONArray());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (obj instanceof JSObject) {
                try {
                    jsonObject.put(key, ((JSObject) obj).toJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
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
        public Object get(TYPE expectedType, String key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JSFunction registerJavaMethod(JavaCallback callback, String jsFunctionName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JSFunction registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Object _executeFunction(TYPE expectedType, String name, JSArray parameters) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return JSValue.TYPE_UNDEFINED;
        }

    }
}
