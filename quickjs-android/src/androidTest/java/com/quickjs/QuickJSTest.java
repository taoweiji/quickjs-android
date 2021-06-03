package com.quickjs;

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

    @Test
    public void createContext2() {
        QuickJS quickJS = QuickJS.createRuntime();
        JSContext context = quickJS.createContext();
        quickJS.close();
    }
}