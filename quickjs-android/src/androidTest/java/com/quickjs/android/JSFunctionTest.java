package com.quickjs.android;

import org.junit.After;
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

    @After
    public void tearDown() throws Exception {
        context.close();
        quickJS.close();
    }

    @Test
    public void testJavaCallback0() {
        context.set("intFunction", new JSFunction(context, (JavaCallback) (receiver, array) -> Integer.MAX_VALUE));
        context.set("doubleFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> Double.MAX_VALUE));
        context.set("boolFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> true));
        context.set("stringFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> "Hello"));

        assertEquals(Integer.MAX_VALUE, context.executeIntegerFunction("intFunction", null));
        assertEquals(Double.MAX_VALUE, context.executeDoubleFunction("doubleFunction", null), 1);
        assertTrue(context.executeBooleanFunction("boolFunction", null));
        assertEquals("Hello", context.executeStringFunction("stringFunction", null));
    }

    @Test
    public void testJavaCallback1() {
        JSObject console = new JSObject(context);
        console.set("intFunction", new JSFunction(context, (JavaCallback) (receiver, array) -> Integer.MAX_VALUE));
        console.set("doubleFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> Double.MAX_VALUE));
        console.set("boolFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> true));
        console.set("stringFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> "Hello"));

        console.executeIntegerFunction("intFunction", null);
        console.executeIntegerFunction("intFunction", null);

//        assertEquals(Integer.MAX_VALUE, context.executeIntegerFunction("intFunction", null));
//        assertEquals(Double.MAX_VALUE, context.executeDoubleFunction("doubleFunction", null), 1);
//        assertTrue(context.executeBooleanFunction("boolFunction", null));
//        assertEquals("Hello", context.executeStringFunction("stringFunction", null));
    }


    @Test
    public void testJavaCallback2() {
        // 以这个为主，解决注入方法
        context.set("intFunction", new JSFunction(context, (JavaCallback) (receiver, array) -> Integer.MAX_VALUE));
        context.set("doubleFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> Double.MAX_VALUE));
        context.set("boolFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> true));
        context.set("stringFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> "Hello"));
        assertEquals(Integer.MAX_VALUE, context.executeIntegerScript("intFunction()", "file.js"));
        assertEquals(Double.MAX_VALUE, context.executeDoubleScript("doubleFunction()", "file.js"), 1);
        assertTrue(context.executeBooleanScript("boolFunction()", null));
        assertEquals("Hello", context.executeStringScript("stringFunction()", "file.js"));
    }

    @Test
    public void testJavaCallback3() {
        context.set("intFunction", new JSFunction(context, (JavaCallback) (receiver, array) -> Integer.MAX_VALUE));
        context.set("doubleFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> Double.MAX_VALUE));
        context.set("boolFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> true));
        context.set("stringFunction", new JSFunction(context, (JavaCallback) (receiver, args) -> "Hello"));

        assertEquals(Integer.MAX_VALUE, ((JSFunction) context.getObject("intFunction")).call(JSValue.TYPE.INTEGER, context, null));
        assertEquals(Double.MAX_VALUE, (Double) ((JSFunction) context.getObject("doubleFunction")).call(JSValue.TYPE.DOUBLE, context, null), 0);
        assertEquals("Hello", ((JSFunction) context.getObject("stringFunction")).call(JSValue.TYPE.STRING, context, null));
        assertTrue((Boolean) ((JSFunction) context.getObject("boolFunction")).call(JSValue.TYPE.BOOLEAN, context, null));
    }


    @Test
    public void testJavaVoidCallback() {
        context.set("test", new JSFunction(context, (receiver, args) -> {
            assertEquals(1, args.getInteger(0));
            assertEquals(3.14, args.getDouble(1), 0);
            assertTrue(args.getBoolean(2));
            assertEquals("Hello", args.getString(3));
        }));
        context.executeVoidScript("test(1, 3.14, true, 'Hello')", "file.js");
    }

    @Test
    public void call0() {
        context.executeVoidScript("function test(data){return 'Hello'}", "file.js");
        JSFunction function = (JSFunction) context.getObject("test");
        function.call(JSValue.TYPE.STRING, context, null);
        function.call(JSValue.TYPE.STRING, context, null);
//        assertEquals("Hello", function.call(JSValue.TYPE.STRING, context, null));
//        assertEquals("Hello", function.call(JSValue.TYPE.STRING, context, null));
    }

    @Test
    public void call1() {
        context.executeVoidScript("function test(data){return 'Hello'}", "file.js");
        assertEquals("Hello", context.executeStringFunction("test", null));
        assertEquals("Hello", context.executeStringFunction("test", null));
    }


    @Test
    public void call2() {
        context.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject receiver, JSArray array) {
                assertEquals("Hello", array.getString(0));
            }
        }, "test");
        JSFunction function = (JSFunction) context.getObject("test");
        JSArray args = new JSArray(context);
        args.push("Hello");
        args.push(3.14);
        function.call(JSValue.TYPE.STRING, context, args);
        function.call(JSValue.TYPE.STRING, context, args);
    }

    @Test
    public void call3() {
        context.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(JSObject receiver, JSArray array) {
                return array.getDouble(1);
            }
        }, "test");
        JSFunction function = (JSFunction) context.getObject("test");
        JSArray parameters = new JSArray(context).push("Hello").push(3.14);
        assertEquals(3.14, function.call(JSValue.TYPE.STRING, context, parameters));
        assertEquals(3.14, function.call(JSValue.TYPE.STRING, context, parameters));
    }
}