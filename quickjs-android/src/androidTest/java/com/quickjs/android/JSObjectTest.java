package com.quickjs.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSObjectTest {

    JSObject object;
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = QuickJS.createRuntime();
        context = quickJS.createContext();
        object = new JSObject(context);
    }

    @After
    public void tearDown() throws Exception {
        object.close();
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
        array.close();
    }

    @Test
    public void getObject() {
        JSObject value = new JSObject(context);
        value.set("name", "Wiki");
        object.set("key1", value);
        assertEquals("Wiki", object.getObject("key1").getString("name"));
        value.close();
    }

    @Test
    public void registerJavaMethod1() {
        String[] result = new String[1];
        object.registerJavaMethod(jsArray -> {
            result[0] = jsArray.getString(0);
        }, "setMessage");
        JSArray jsArray = new JSArray(context);
        jsArray.push("Hello");
        object.executeVoidFunction("setMessage", jsArray);
        assertEquals("Hello", result[0]);
    }

    @Test
    public void registerJavaMethod2() {
    }

    @Test
    public void executeFunction() {

    }

    @Test
    public void executeIntegerFunction() {
        context.executeVoidScript("function test(data){ return data[0]}", "file.js");
        JSArray array = new JSArray(context);
        array.push(Integer.MAX_VALUE);
        int result = context.executeIntegerFunction("test", array);
        array.close();
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void executeDoubleFunction() {
        context.executeVoidScript("function test(data){ return data[0]}", "file.js");
        JSArray array = new JSArray(context);
        array.push(3.14);
        double result = context.executeDoubleFunction("test", array);
        array.close();
        assertEquals(3.14, result, 0);
    }

    @Test
    public void executeBooleanFunction() {
        context.executeVoidScript("function test(data){ return data[0]}", "file.js");
        JSArray array = new JSArray(context);
        array.push(true);
        boolean result = context.executeBooleanFunction("test", array);
        array.close();
        assertTrue(result);
    }

    @Test
    public void executeStringFunction() {
        context.executeVoidScript("function test(data){ return data[0]}", "file.js");
        JSArray array = new JSArray(context);
        array.push("Hello");
        String result = context.executeStringFunction("test", array);
        array.close();
        assertEquals("Hello", result);
    }

    @Test
    public void executeArrayFunction() {
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        array.push("Hello");
        JSArray result = context.executeArrayFunction("test", array);
        array.close();
        assertEquals("Hello", result.getString(0));
    }

    @Test
    public void executeObjectFunction() {
        context.executeVoidScript("function test(data){ return data[0]}", "file.js");
        JSArray array = new JSArray(context);
        JSObject value = new JSObject(context);
        value.set("name", "Wiki");
        array.push(value);
        JSArray result = (JSArray) context.executeObjectFunction("test", array);
        array.close();
        assertEquals("Hello", result.getString("name"));
    }

    @Test
    public void executeVoidFunction() {
    }

    @Test
    public void executeJSFunction() {

    }

    @Test
    public void testExecuteJSFunction() {
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
}