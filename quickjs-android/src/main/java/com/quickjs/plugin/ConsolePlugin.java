package com.quickjs.plugin;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSObject;
import com.quickjs.JSValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsolePlugin extends Plugin {
    private int count;
    private final Map<String, Long> timer = new HashMap<>();

    @Override
    public void setup(JSContext context) {
        JSObject console = context.addJavascriptInterface(this, "console");
        console.registerJavaMethod((receiver, args) -> {
            if (!args.getBoolean(0)) {
                error(args.getString(1));
            }
        }, "assert");
    }

    @Override
    public void close(JSContext context) {

    }

    @JavascriptInterface
    public final void log(String msg) {
        count++;
        println(Log.DEBUG, msg);
    }

    @JavascriptInterface
    public final void info(String msg) {
        count++;
        println(Log.INFO, msg);
    }

    @JavascriptInterface
    public final void error(String msg) {
        count++;
        println(Log.ERROR, msg);
    }

    @JavascriptInterface
    public final void warn(String msg) {
        count++;
        println(Log.WARN, msg);
    }

    public void println(int priority, String msg) {
        Log.println(priority, "QuickJS", msg);
    }

    @JavascriptInterface
    public final int count() {
        return count;
    }


    @JavascriptInterface
    public final void table(JSObject obj) {
        if (obj instanceof JSArray) {
            try {
                log(toJsonArray((JSArray) obj).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (obj != null) {
            try {
                log(toJsonObject(obj).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject toJsonObject(JSObject obj) throws JSONException {
        if (obj == null) return null;
        JSONObject json = new JSONObject();
        String[] keys = obj.getKeys();
        for (String key : keys) {
            Object tmp = obj.get(JSValue.TYPE.UNKNOWN, key);
            if (tmp instanceof JSArray) {
                json.put(key, toJsonArray((JSArray) tmp));
            } else if (tmp instanceof JSObject) {
                json.put(key, toJsonObject((JSObject) tmp));
            } else {
                json.put(key, tmp);
            }
        }
        return json;
    }

    private JSONArray toJsonArray(JSArray array) throws JSONException {
        if (array == null) return null;
        JSONArray json = new JSONArray();
        int length = array.length();
        for (int i = 0; i < length; i++) {
            Object tmp = array.get(JSValue.TYPE.UNKNOWN, i);
            if (tmp instanceof JSArray) {
                json.put(toJsonArray((JSArray) tmp));
            } else if (tmp instanceof JSObject) {
                json.put(toJsonObject((JSObject) tmp));
            } else {
                json.put(tmp);
            }
        }
        return json;
    }


    @JavascriptInterface
    public final void time(String name) {
        if (timer.containsKey(name)) {
            warn(String.format("Timer '%s' already exists", name));
            return;
        }
        timer.put(name, System.currentTimeMillis());
    }

    @JavascriptInterface
    public final void timeEnd(String name) {
        Long startTime = timer.get(name);
        if (startTime != null) {
            float ms = (System.currentTimeMillis() - startTime);
            log(String.format("%s: %s ms", name, ms));
        }
        timer.remove(name);
    }

    @JavascriptInterface
    public void trace() {
        log("This 'console.trace' function is not supported");
    }

    @JavascriptInterface
    public void clear() {
        log("This 'console.clear' function is not supported");
    }

    @JavascriptInterface
    public void group(String name) {
        log("This 'console.group' function is not supported");
    }

    @JavascriptInterface
    public void groupCollapsed(String name) {
        log("This 'console.groupCollapsed' function is not supported");
    }

    @JavascriptInterface
    public void groupEnd(String name) {
        log("This 'console.groupEnd' function is not supported");
    }
}
