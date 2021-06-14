package com.quickjs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSArray extends JSObject {

    public JSArray(JSContext context) {
        super(context, context.getNative()._initNewJSArray(context.getContextPtr()));
    }

    JSArray(JSContext context, long tag, int u_int32, double u_float64, long u_ptr) {
        super(context, tag, u_int32, u_float64, u_ptr);
    }

    public JSArray(JSContext context, JSONArray jsonArray) {
        this(context);
        append(this, jsonArray);
    }

    public static void append(JSArray jsArray, JSONArray jsonArray) {
        if (jsonArray == null) {
            return;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            Object obj = jsonArray.opt(i);
            if (obj instanceof String) {
                jsArray.push((String) obj);
            } else if (obj instanceof Integer) {
                jsArray.push((Integer) obj);
            } else if (obj instanceof Boolean) {
                jsArray.push((Boolean) obj);
            } else if (obj instanceof Number) {
                jsArray.push(((Number) obj).doubleValue());
            } else if (obj instanceof JSONObject) {
                jsArray.push(new JSObject(jsArray.context, (JSONObject) obj));
            } else if (obj instanceof JSONArray) {
                jsArray.push(new JSArray(jsArray.context, (JSONArray) obj));
            }
        }
    }

    public Object get(int index) {
        return get(TYPE.UNKNOWN, index);
    }

    Object get(TYPE expectedType, int index) {
        this.context.checkReleased();
        if (expectedType == null) {
            expectedType = TYPE.UNKNOWN;
        }
        Object object = getNative()._arrayGet(this.getContextPtr(), expectedType.value, this, index);
        return JSValue.checkType(object, expectedType);
    }

    JSArray pushObject(Object value) {
        this.context.checkReleased();
        getNative()._arrayAdd(getContextPtr(), this, value);
        return this;
    }

    public int getInteger(int index) {
        Object result = get(JSValue.TYPE.INTEGER, index);
        if (result instanceof Integer) {
            return (int) result;
        }
        return 0;
    }

    public boolean getBoolean(int index) {
        Object result = get(JSValue.TYPE.BOOLEAN, index);
        if (result instanceof Boolean) {
            return (boolean) result;
        }
        return false;
    }

    public double getDouble(int index) {
        Object result = get(JSValue.TYPE.DOUBLE, index);
        if (result instanceof Double) {
            return (double) result;
        }
        return 0;
    }

    public String getString(int index) {
        Object result = get(JSValue.TYPE.STRING, index);
        if (result instanceof String) {
            return (String) result;
        }
        return null;
    }

    public JSObject getObject(int index) {
        Object result = get(JSValue.TYPE.JS_OBJECT, index);
        if (result instanceof JSObject) {
            return (JSObject) result;
        }
        return null;
    }

    public JSArray getArray(int index) {
        Object result = get(JSValue.TYPE.JS_ARRAY, index);
        if (result instanceof JSArray) {
            return (JSArray) result;
        }
        return null;
    }

    public TYPE getType(int index) {
        this.context.checkReleased();
        JSValue value = getContext().getNative()._arrayGetValue(this.getContextPtr(), this, index);
        if (value == null) {
            return JSValue.TYPE.NULL;
        }
        return value.getType();
    }

    public JSArray push(int value) {
        return pushObject(value);
    }

    public JSArray push(double value) {
        return pushObject(value);
    }

    public JSArray push(String value) {
        return pushObject(value);
    }

    public JSArray push(boolean value) {
        return pushObject(value);
    }

    public JSArray push(JSValue value) {
        this.context.checkRuntime(value);
        return pushObject(value);
    }

    public int length() {
        return getInteger("length");
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < this.length(); i++) {
            Object obj = this.get(i);
            if (obj instanceof Undefined || obj instanceof JSFunction) {
                continue;
            }
            if (obj instanceof Number || obj instanceof String || obj instanceof Boolean) {
                jsonArray.put(obj);
            } else if (obj instanceof JSArray) {
                jsonArray.put(((JSArray) obj).toJSONArray());
            } else if (obj instanceof JSObject) {
                jsonArray.put(((JSObject) obj).toJSONObject());
            }
        }
        return jsonArray;
    }
}
