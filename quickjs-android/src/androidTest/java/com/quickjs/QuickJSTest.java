package com.quickjs;

import org.junit.Test;

public class QuickJSTest  extends BaseTest{

    @Test
    public void createRuntime() {
        QuickJS quickJS = createQuickJS();
        quickJS.close();
    }

    @Test
    public void createContext() {
        QuickJS quickJS = createQuickJS();
        JSContext context = quickJS.createContext();
        context.close();
        quickJS.close();
    }

    @Test
    public void createContext2() {
        QuickJS quickJS = createQuickJS();
        JSContext context = quickJS.createContext();
        quickJS.close();
    }
}