package com.quickjs.android;

import android.util.Log;

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
    public void testJavaCallback() {
        JSFunction jsFunction = new JSFunction(context, new JavaCallback() {
            @Override
            public Object invoke(JSObject jsObject, JSArray jsArray) {
                return 1228;
            }
        });
        context.set("test", jsFunction);
        int result = context.executeIntegerFunction("test", null);
//        int result = context.executeIntegerScript("test()", "file.js");
        assertEquals(1228, result);
    }

    @Test
    public void testJavaVoidCallback() {
        JSFunction jsFunction = new JSFunction(context, new JavaVoidCallback() {
            @Override
            public void invoke(JSObject jsObject, JSArray jsArray) {
                Log.e("testJavaVoidCallback", jsArray.getString(0));
            }
        });
        context.set("test", jsFunction);
        context.executeVoidScript("test('Hello')", "file.js");
    }

}