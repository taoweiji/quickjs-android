package com.quickjs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JSArrayTest  extends BaseTest{
    JSArray array;
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() {
        quickJS = QuickJS.createRuntime();
        context = quickJS.createContext();
        array = new JSArray(context);
    }

    @After
    public void tearDown() {
        context.close();
        quickJS.close();
    }

    @Test
    public void getInteger() {
        array.push(1).push(Integer.MAX_VALUE).push(Integer.MIN_VALUE);
        assertEquals(1, array.getInteger(0));
        assertEquals(Integer.MAX_VALUE, array.getInteger(1));
        assertEquals(Integer.MIN_VALUE, array.getInteger(2));
    }

    @Test
    public void getBoolean() {
        array.push(true).push(false);
        assertTrue(array.getBoolean(0));
        assertFalse(array.getBoolean(1));
    }

    @Test
    public void getDouble() {
        long time = System.currentTimeMillis();
        array.push(1.0).push(Double.MAX_VALUE).push(Double.MIN_VALUE).push(time);
        assertEquals(1.0, array.getDouble(0), 0.01);
        assertEquals(Double.MAX_VALUE, array.getDouble(1), 0.01);
        assertEquals(Double.MIN_VALUE, array.getDouble(2), 0.01);
        assertEquals(time, array.getDouble(3), 0);
        assertEquals(JSValue.TYPE.DOUBLE, array.getType(3));
    }

    @Test
    public void getString() {
        array.push("Hello").push("World").push("Wiki");
        assertEquals("Hello", array.getString(0));
        assertEquals("World", array.getString(1));
        assertEquals("Wiki", array.getString(2));
    }


    @Test
    public void length() {
        array.push(1).push(true).push(1.0).push("Hello");
        assertEquals(4, array.length());
    }

    @Test
    public void getObject() {
        array.push(new JSObject(context).set("name", "Wiki"));
        assertEquals("Wiki", array.getObject(0).getString("name"));
    }

    @Test
    public void getArray() {
        array.push(new JSArray(context).push("Wiki"));
        assertEquals("Wiki", array.getArray(0).getString(0));
    }

    @Test
    public void getType() {
        JSArray array = new JSArray(context).push("1").push(1).push(true).push(1.1);
        assertEquals(JSValue.TYPE.STRING, array.getType(0));
        assertEquals(JSValue.TYPE.INTEGER, array.getType(1));
        assertEquals(JSValue.TYPE.BOOLEAN, array.getType(2));
        assertEquals(JSValue.TYPE.DOUBLE, array.getType(3));
    }
}