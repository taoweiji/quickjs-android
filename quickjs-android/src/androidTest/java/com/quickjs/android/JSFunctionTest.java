package com.quickjs.android;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSFunctionTest {

    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = QuickJS.createRuntime();
        context = quickJS.createContext();
    }

    @Test
    public void call() {
        JSFunction jsFunction = new JSFunction(context, new JavaCallback() {
            @Override
            public Object invoke(JSObject jsObject, JSArray jsArray) {
                return null;
            }
        });
        context.set("test", jsFunction);
        int result = context.executeIntegerFunction("test", null);
        assertEquals(1228, result);
    }
}