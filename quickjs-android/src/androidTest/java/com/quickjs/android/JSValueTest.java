package com.quickjs.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSValueTest {
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = QuickJS.createRuntime();
        context = quickJS.createContext();
    }

    @After
    public void tearDown() throws Exception {
//        context.close();
//        quickJS.close();
    }

    @Test
    public void isUndefined() {
    }

    @Test
    public void getJSType() {
    }

    @Test
    public void undefined() {
    }

    @Test
    public void testEquals() {
        JSArray array = new JSArray(context);
        assertEquals(JSValue.Undefined(context), array.getObject(0));
        assertTrue(array.getObject(0).isUndefined());
        array.close();
    }

    @Test
    public void testHashCode() {
    }
}