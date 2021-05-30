package com.quickjs.android;

import org.junit.Test;

import static org.junit.Assert.*;

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