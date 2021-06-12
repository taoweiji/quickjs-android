package com.quickjs;

import org.junit.Test;

public class QuickJSTest extends BaseTest {

    @Test
    public void createRuntimeWithEventQueue() {
        QuickJS quickJS = QuickJS.createRuntimeWithEventQueue();
        JSContext context = quickJS.createContext();
        for (int i = 0; i < 1000; i++) {
            new JSObject(context);
        }
        quickJS.close();
    }

    @Test
    public void createRuntime() {
        QuickJS quickJS = QuickJS.createRuntime();
        JSContext context = quickJS.createContext();
        for (int i = 0; i < 1000; i++) {
            new JSObject(context);
        }
        quickJS.close();
    }


    @Test
    public void createContext() {
        QuickJS quickJS = QuickJS.createRuntimeWithEventQueue();
        JSContext context = quickJS.createContext();
        context.close();
        quickJS.close();
    }

    @Test
    public void createContext2() {
        QuickJS quickJS = QuickJS.createRuntimeWithEventQueue();
        JSContext context = quickJS.createContext();
        quickJS.close();
    }
}