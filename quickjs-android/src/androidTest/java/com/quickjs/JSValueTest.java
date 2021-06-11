package com.quickjs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSValueTest  extends BaseTest{
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = createQuickJS();
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
        context.executeVoidScript("var a = 1111111111111;a", null);
        assertEquals(JSValue.TYPE.DOUBLE, context.getType("a"));
        assertEquals(1111111111111L, context.getDouble("a"), 0);
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