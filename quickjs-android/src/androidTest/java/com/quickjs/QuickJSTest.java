package com.quickjs;

import com.quickjs.JSContext;
import com.quickjs.QuickJS;

import org.junit.Test;

public class QuickJSTest {

    @Test
    public void createRuntime() {
        QuickJS quickJS = QuickJS.createRuntime();
        quickJS.close();
    }

    @Test
    public void createContext() {
        QuickJS quickJS = QuickJS.createRuntime();
        JSContext context = quickJS.createContext();
        context.close();
        quickJS.close();
    }

}