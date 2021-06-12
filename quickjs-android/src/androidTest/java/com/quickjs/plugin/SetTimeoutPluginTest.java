package com.quickjs.plugin;

import com.quickjs.BaseTest;
import com.quickjs.JSContext;
import com.quickjs.QuickJS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SetTimeoutPluginTest extends BaseTest {
    List<String> logs = new ArrayList<>();

    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() {
        quickJS = QuickJS.createRuntimeWithEventQueue();
        context = quickJS.createContext();
        context.addPlugin(new ConsolePlugin() {
            @Override
            public void println(int priority, String msg) {
                super.println(priority, msg);
                logs.add(msg);
            }
        });
        context.addPlugin(new SetTimeoutPlugin());
    }

    @After
    public void tearDown() {
        context.close();
        quickJS.close();
    }

    @Test
    public void post() throws InterruptedException {
        context.executeVoidScript("var counter = 0;\n" +
                "function print1() {\n" +
                "    console.log(counter++);\n" +
                "    setTimeout(print1, 10);\n" +
                "}\n" +
                "print1();", null);
        Thread.sleep(200);
        assertTrue(logs.size() > 10);
    }


    @Test
    public void postDelayed() throws InterruptedException {
        context.executeVoidScript("setTimeout(function(){console.log('Hello')}, 100);", null);
        assertTrue(logs.isEmpty());
        Thread.sleep(200);
        assertEquals(1, logs.size());
    }
}