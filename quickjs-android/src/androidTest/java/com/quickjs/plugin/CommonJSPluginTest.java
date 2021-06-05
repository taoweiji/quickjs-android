package com.quickjs.plugin;

import com.quickjs.JSContext;
import com.quickjs.QuickJS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CommonJSPluginTest {
    private JSContext context;
    private QuickJS quickJS;
    private Map<String, String> fileMap = new HashMap<>();

    @Before
    public void setUp() throws Throwable {
        quickJS = QuickJS.createRuntime();
        context = quickJS.createContext();
        context.addPlugin(new CommonJSPlugin() {
            @Override
            public String require(Module parent, String path) {
                return fileMap.get(path);
            }
        });
    }

    @After
    public void tearDown() throws Throwable {
        context.close();
        quickJS.close();
    }

    @Test
    public void require() {
        fileMap.put("a.js", "module.exports = 'Hello world';");
        Object result = context.executeScript("var result = require('a.js');result;", null);
        assertEquals("Hello world", result);

        Object result2 = context.executeScript("var result = require('a.js');result;", null);
        assertEquals("Hello world", result2);
    }

    @Test
    public void getContext() {

    }
}