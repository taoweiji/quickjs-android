package com.quickjs.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSObjectTest {

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
    }

    @Test
    public void getBoolean() {
    }

    @Test
    public void getDouble() {
    }

    @Test
    public void getString() {
    }

    @Test
    public void getArray() {
    }

    @Test
    public void getObject() {
    }

    @Test
    public void registerJavaMethod() {
    }

    @Test
    public void testRegisterJavaMethod() {
    }

    @Test
    public void executeFunction() {
    }

    @Test
    public void executeIntegerFunction() {
    }

    @Test
    public void executeDoubleFunction() {
    }

    @Test
    public void executeBooleanFunction() {
    }

    @Test
    public void executeStringFunction() {
    }

    @Test
    public void executeArrayFunction() {
    }

    @Test
    public void executeObjectFunction() {
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
    }

    @Test
    public void getKeys() {
    }
}