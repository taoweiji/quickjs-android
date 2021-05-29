package com.quickjs.android;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void call() {
        context.executeVoidScript("function test(data){return 'Hello'}", "file.js");
        JSFunction function = (JSFunction) context.getObject("test");
        String result = (String) function.call(context, null);
        assertEquals("Hello", result);
    }

    @Test
    public void call2() {
        context.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSArray array) {
                assertEquals("Hello", array.getString(0));
            }
        }, "test");
        JSFunction function = (JSFunction) context.getObject("test");
        JSArray args = new JSArray(context);
        args.push("Hello");
        args.push(3.14);
        function.call(context, args);
    }

    @Test
    public void call3() {
        context.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(JSArray array) {
                return array.getDouble(1);
            }
        }, "test");
        JSFunction function = (JSFunction) context.getObject("test");
        JSArray parameters = new JSArray(context).push("Hello").push(3.14);
        Object result = function.call(context, parameters);
        assertEquals(3.14, result);
    }


}