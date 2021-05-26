package com.quickjs.android;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class JSFunctionTest {

    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = QuickJS.createRuntime();
        context = quickJS.createContext();
    }

    @Test
    public void testJavaCallback() {
        context.set("intFunction", new JSFunction(context, jsArray -> Integer.MAX_VALUE));
        context.set("doubleFunction", new JSFunction(context, jsArray -> Double.MAX_VALUE));
        context.set("boolFunction", new JSFunction(context, jsArray -> true));
        context.set("stringFunction", new JSFunction(context, jsArray -> "Hello"));
        assertEquals(Integer.MAX_VALUE, context.executeIntegerFunction("intFunction", null));
        assertEquals(Double.MAX_VALUE, context.executeDoubleFunction("doubleFunction", null), 1);
        assertTrue(context.executeBooleanFunction("boolFunction", null));
        assertEquals("Hello", context.executeStringFunction("stringFunction", null));
    }


    @Test
    public void testJavaVoidCallback() {
        context.set("test", new JSFunction(context, array -> {
            assertEquals(1, array.getInteger(0));
            assertEquals(3.14, array.getDouble(1), 0);
            assertTrue(array.getBoolean(2));
            assertEquals("Hello", array.getString(3));
        }));
        context.executeVoidScript("test(1, 3.14, true, 'Hello')", "file.js");
    }
}