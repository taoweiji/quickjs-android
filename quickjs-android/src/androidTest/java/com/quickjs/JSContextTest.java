package com.quickjs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSContextTest  extends BaseTest{
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() {
        quickJS = createQuickJS();
        context = quickJS.createContext();
    }

    @After
    public void tearDown() {
        context.close();
        quickJS.close();
    }

    @Test
    public void doubleContext() {
        context = quickJS.createContext();
        context = quickJS.createContext();
    }


    @Test
    public void executeScript() {
        Object result1 = context.executeScript("function test(data){return data};test(123)", "file.js");
        assertEquals(123, result1);
        Object result2 = context.executeScript("function test(data){return data};test(true)", "file.js");
        assertEquals(true, result2);
        Object result3 = context.executeScript("function test(data){return data};test(3.14)", "file.js");
        assertEquals(3.14, result3);
        Object result4 = context.executeScript("function test(data){return data};test('hello')", "file.js");
        assertEquals("hello", result4);
        Object result5 = context.executeScript("function test(data){return data};test(this)", "file.js");
        assertEquals(JSObject.class, result5.getClass());
        Object result6 = context.executeScript("function test(data){return data};test([''])", "file.js");
        assertEquals(JSArray.class, result6.getClass());
        Object result7 = context.executeScript("function test(data){return data};test", "file.js");
        assertEquals(JSFunction.class, result7.getClass());
    }

    @Test
    public void executeIntegerScript() {
        int result1 = context.executeIntegerScript("function test(data){return data};test(123)", "file.js");
        assertEquals(123, result1);
    }

    @Test
    public void executeDoubleScript() {
        double result3 = context.executeDoubleScript("function test(data){return data};test(3.14)", "file.js");
        assertEquals(3.14, result3, 0);
    }

    @Test
    public void executeBooleanScript() {
        boolean result2 = context.executeBooleanScript("function test(data){return data};test(true)", "file.js");
        assertTrue(result2);

    }

    @Test
    public void executeStringScript() {
        String result4 = context.executeStringScript("function test(data){return data};test('hello')", "file.js");
        assertEquals("hello", result4);
    }

    @Test
    public void executeVoidScript() {
        context.executeVoidScript("function test(data){return data};test('hello')", "file.js");
    }

    @Test
    public void executeArrayScript() {
        JSArray result6 = context.executeArrayScript("function test(data){return data};test([''])", "file.js");
        assertEquals(JSArray.class, result6.getClass());
    }

    @Test
    public void executeObjectScript() {
        JSObject result5 = context.executeObjectScript("function test(data){return data};test(this)", "file.js");
        assertEquals(JSObject.class, result5.getClass());
        Object result7 = context.executeScript("function test(data){return data};test", "file.js");
        assertEquals(JSFunction.class, result7.getClass());
    }


    @Test
    public void executeObjectScriptException() {
        try {
            context.executeVoidScript("function test1(params) {\n" +
                    "    params.say();\n" +
                    "}\n" +
                    "function test2(params) {\n" +
                    "    test1(params);\n" +
                    "}\n" +
                    "function test3(params) {\n" +
                    "    test2(params);\n" +
                    "}\n" +
                    "test3(\"\");", "file.js");
            throw new Exception();
        } catch (Exception e) {
            assertEquals(QuickJSException.class, e.getClass());
        }
    }
}