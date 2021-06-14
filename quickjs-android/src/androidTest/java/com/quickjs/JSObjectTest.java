package com.quickjs;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JSObjectTest extends BaseTest {

    JSObject object;
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = createQuickJS();
        context = quickJS.createContext();
        object = new JSObject(context);
    }

    @After
    public void tearDown() throws Throwable {
//        System.gc();
//        System.gc();
//        Thread.sleep(5000);
        context.close();
        quickJS.close();
    }

    @Test
    public void getInteger() {
        object.set("key1", Integer.MAX_VALUE);
        object.set("key2", Integer.MIN_VALUE);
        object.set("key3", 1);
        assertEquals(Integer.MAX_VALUE, object.getInteger("key1"));
        assertEquals(Integer.MIN_VALUE, object.getInteger("key2"));
        assertEquals(1, object.getInteger("key3"));
    }

    @Test
    public void getBoolean() {
        object.set("key1", true);
        object.set("key2", false);
        assertTrue(object.getBoolean("key1"));
        assertFalse(object.getBoolean("key2"));
    }

    @Test
    public void getDouble() {
        object.set("key1", Double.MAX_VALUE);
        object.set("key2", Double.MIN_VALUE);
        object.set("key3", 1);
        assertEquals(Double.MAX_VALUE, object.getDouble("key1"), 1);
        assertEquals(Double.MIN_VALUE, object.getDouble("key2"), 1);
        assertEquals(1, object.getDouble("key3"), 0);
    }

    @Test
    public void getString() {
        object.set("key1", "Hello");
        object.set("key2", "World");
        assertEquals("Hello", object.getString("key1"));
        assertEquals("World", object.getString("key2"));
    }

    @Test
    public void getArray() {
        JSArray array = new JSArray(context);
        array.push(1);
        object.set("key1", array);
        assertEquals(1, object.getArray("key1").getInteger(0));
    }

    @Test
    public void getObject() {
        JSObject value = new JSObject(context);
        value.set("name", "Wiki");
        object.set("key1", value);
        JSObject value2 = object.getObject("key1");
        value.set("age", 18);
        assertEquals("Wiki", value2.getString("name"));
        assertEquals(18, value2.getInteger("age"));
    }


    @Test
    public void executeIntegerFunction() {
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        array.push(Integer.MAX_VALUE);
        int result = context.executeIntegerFunction("test", array);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void executeDoubleFunction() {
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        array.push(3.14);
        assertEquals(3.14, context.executeDoubleFunction("test", array), 0);
    }

    @Test
    public void executeBooleanFunction() {
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        array.push(true);
        boolean result = context.executeBooleanFunction("test", array);
        assertTrue(result);
    }

    @Test
    public void executeStringFunction() {
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        array.push("Hello");
        String result = context.executeStringFunction("test", array);
        assertEquals("Hello", result);
    }

    @Test
    public void executeArrayFunction() {
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        JSArray arg = new JSArray(context);
        arg.push("Hello");
        array.push(arg);
        JSArray result = context.executeArrayFunction("test", array);
        assertEquals("Hello", result.getString(0));
    }

    @Test
    public void executeObjectFunction() {
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        JSObject value = new JSObject(context);
        value.set("name", "Wiki");
        array.push(value);
        JSObject result = context.executeObjectFunction("test", array);
        assertEquals("Wiki", result.getString("name"));
    }


    @Test
    public void contains() {
        object.set("name", "Wiki");
        object.set("age", 18);
        assertTrue(object.contains("name"));
        assertTrue(object.contains("age"));
        assertFalse(object.contains("age2"));
    }

    @Test
    public void getKeys() {
        object.set("name", "Wiki");
        object.set("age", 18);
        String[] result = object.getKeys();
        assertEquals("name", result[0]);
        assertEquals("age", result[1]);
        assertEquals(2, result.length);
    }

    @Test
    public void executeFunction2() {
        context.executeVoidScript("function test1(arg1,arg2){ return arg1}", "file.js");
        context.executeVoidScript("function test2(arg1,arg2){ return arg2}", "file2.js");
        assertEquals("Hello", context.executeFunction2("test1", "Hello", Integer.MAX_VALUE));
        assertEquals("H", context.executeFunction2("test2", "Hello", "H"));
        assertEquals(true, context.executeFunction2("test2", "Hello", true));
        assertEquals(Integer.MAX_VALUE, context.executeFunction2("test2", "Hello", Integer.MAX_VALUE));
    }

    @Test
    public void console() {
        final String[] tmp = new String[1];
        JSObject console = new JSObject(context);
        console.registerJavaMethod((receiver, args) -> {
            tmp[0] = args.getString(0);
        }, "log");
        context.set("console", console);
        context.executeVoidScript("console.log('Hello')", "file.js");
        assertEquals("Hello", tmp[0]);
    }

    @Test
    public void registerJavaMethod() {
        final String[] tmp = new String[1];
        context.registerJavaMethod((receiver, args) -> {
            tmp[0] = args.getString(0);
        }, "log");
        context.executeVoidScript("log('Hello')", "file.js");
        assertEquals("Hello", tmp[0]);

        context.executeVoidScript("log('Hello')", "file.js");
        assertEquals("Hello", tmp[0]);


        JSObject user = new JSObject(context).set("name", "Wiki").set("age", 18);
        user.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject receiver, JSArray args) {
                Log.e("QuickJS", args.getString(0));
            }
        }, "log");
        user.executeVoidFunction("log", new JSArray(context).push("Hello World"));
    }

    @Test
    public void registerJavaMethod2() {
        context.registerJavaMethod((JavaCallback) (receiver, array) -> Integer.MAX_VALUE, "log");
        assertEquals(Integer.MAX_VALUE, context.executeIntegerScript("log()", "file.js"));
        assertEquals(Integer.MAX_VALUE, context.executeIntegerScript("log()", "file.js"));
//        context.executeVoidScript("log()", "file.js");
//        context.executeVoidScript("log()", "file.js");
    }


    @Test
    public void getType() {
        context.set("key1", "1");
        context.set("key2", 1);
        context.set("key3", true);
        context.set("key4", 1.1);
        assertEquals(JSValue.TYPE.STRING, context.getType("key1"));
        assertEquals(JSValue.TYPE.INTEGER, context.getType("key2"));
        assertEquals(JSValue.TYPE.BOOLEAN, context.getType("key3"));
        assertEquals(JSValue.TYPE.DOUBLE, context.getType("key4"));
    }

    @Test
    public void test2() {
        JSObject array = new JSObject(context);
        JSObject value = new JSObject(context);
        value.set("name", "Wiki");
        array.set("www", value);
        array.set("www", JSValue.NULL());
    }

    @Test
    public void executeVoidFunction() {

    }

    @Test
    public void addJavascriptInterface() {
        context.addJavascriptInterface(new Console(), "console");
        context.executeVoidScript("console.log('Hello World')", null);
        int count = context.executeIntegerScript("console.count()", null);
        assertEquals(1, count);
    }

    @Test
    public void executeFunction() {
        final long time = System.currentTimeMillis();
        context.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(JSObject receiver, JSArray args) {
                return time;
            }
        }, "time");
        assertEquals(time, (Double) context.executeFunction("time", null), 0);
    }

    @Test
    public void appendJavascriptInterface() {
        context.appendJavascriptInterface(new Console());
        context.executeVoidScript("log('Hello World')", null);
        context.executeVoidScript("error('Hello World')", null);
        int count = context.executeIntegerScript("count()", null);
        assertEquals(2, count);
    }


    @Test
    public void constructor() throws JSONException {
        JSObject object = new JSObject(context, new JSONObject("{\"name\":\"Wiki\",\"age\":18,\"body\":{\"a\":\"Hello\"},\"likes\":[1,\"Hello\",{\"b\":\"b\"}]}"));
        assertEquals("Wiki", object.getString("name"));
        assertEquals(18, object.getInteger("age"));
        assertEquals("Hello", object.getObject("body").getString("a"));
        assertEquals(1, object.getArray("likes").getInteger(0));
        assertEquals("Hello", object.getArray("likes").getString(1));
        assertEquals("b", object.getArray("likes").getObject(2).getString("b"));
    }

    @Test
    public void registerClass() {
        Object[] result = new Object[1];
        context.registerClass((thisObj, args) -> result[0] = args.getString(0), "Worker");
        context.executeVoidScript("var worker = new Worker('Hello')", null);
        assertEquals("Hello", result[0]);
    }

    @Test
    public void toJSONObject() throws JSONException {
        JSObject body = new JSObject(context).set("a", "Hello");
        JSArray likes = new JSArray(context).push(1).push("Hello").push(new JSObject(context).set("b", "b"));
        JSObject user = new JSObject(context).set("name", "Wiki").set("age", 18).set("body", body).set("likes", likes);
        JSONObject jsonObject = user.toJSONObject();
        assertEquals("Wiki", jsonObject.getString("name"));
        assertEquals(18, jsonObject.getInt("age"));
        assertEquals("Hello", jsonObject.getJSONObject("body").getString("a"));
        assertEquals(1, jsonObject.getJSONArray("likes").getInt(0));
        assertEquals("Hello", jsonObject.getJSONArray("likes").getString(1));
        assertEquals("b", jsonObject.getJSONArray("likes").getJSONObject(2).getString("b"));
    }

    public static class Console {
        int count = 0;

        @JavascriptInterface
        public void log(String msg) {
            count++;
            Log.d("console", msg);
        }

        @JavascriptInterface
        public void info(String msg) {
            count++;
            Log.i("console", msg);
        }

        @JavascriptInterface
        public void error(String msg) {
            count++;
            Log.e("console", msg);
        }

        @JavascriptInterface
        public int count() {
            return count;
        }
    }
}