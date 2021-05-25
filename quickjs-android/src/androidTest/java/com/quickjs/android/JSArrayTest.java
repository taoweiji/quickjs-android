package com.quickjs.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSArrayTest {
    JSArray array;
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = QuickJS.createRuntime();
        context = quickJS.createContext();
        array = new JSArray(context);
    }

    @After
    public void tearDown() throws Exception {
        array.close();
        context.close();
        quickJS.close();
    }

    @Test
    public void getInteger() {
        array.push(1);
        array.push(Integer.MAX_VALUE);
        array.push(Integer.MIN_VALUE);
        assertEquals(1, array.getInteger(0));
        assertEquals(Integer.MAX_VALUE, array.getInteger(1));
        assertEquals(Integer.MIN_VALUE, array.getInteger(2));
    }

    @Test
    public void getBoolean() {
        array.push(true);
        array.push(false);
        assertTrue(array.getBoolean(0));
        assertFalse(array.getBoolean(1));
    }

    @Test
    public void getDouble() {
        array.push(1.0);
        array.push(Double.MAX_VALUE);
        array.push(Double.MIN_VALUE);
        assertEquals(1.0, array.getDouble(0), 1);
        assertEquals(Double.MAX_VALUE, array.getDouble(1), 1);
        assertEquals(Double.MIN_VALUE, array.getDouble(2), 1);
    }

    @Test
    public void getString() {
        array.push("Hello");
        array.push("World");
        array.push("Wiki");
        assertEquals("Hello", array.getString(0));
        assertEquals("World", array.getString(1));
        assertEquals("Wiki", array.getString(2));
    }


    @Test
    public void length() {
        array.push(1);
        array.push(true);
        array.push(1.0);
        array.push("Hello");
        assertEquals(4, array.length());
    }
}