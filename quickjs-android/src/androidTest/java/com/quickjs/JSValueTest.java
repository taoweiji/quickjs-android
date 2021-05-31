package com.quickjs;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSValue;
import com.quickjs.QuickJS;

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
        context.close();
        quickJS.close();
    }

    @Test
    public void isUndefined() {
        JSArray array = new JSArray(context);
        assertTrue(array.getObject(0).isUndefined());
    }

    @Test
    public void getType() {
        assertEquals(JSValue.TYPE.UNDEFINED, JSValue.Undefined(context).getType());
    }

    @Test
    public void undefined() {
        assertTrue(JSValue.Undefined(context).isUndefined());
    }

    @Test
    public void testEquals() {
        JSArray array = new JSArray(context);
        assertEquals(JSValue.Undefined(context), array.getObject(0));
    }

    @Test
    public void testHashCode() {
        JSArray array = new JSArray(context);
        assertEquals(JSValue.Undefined(context).hashCode(), array.getObject(0).hashCode());
    }
}