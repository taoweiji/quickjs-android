package com.quickjs;

import com.quickjs.plugin.ConsolePlugin;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventQueueTest {
    EventQueue eventQueue;

    @Before
    public void setUp() {
        eventQueue = new EventQueue() {
            @Override
            protected void setup(JSContext context) {
                super.setup(context);
                context.addPlugin(new ConsolePlugin() {
                    @Override
                    public void println(int priority, String msg) {
                        super.println(priority, msg);
                    }
                });
            }

            @Override
            protected JSContext createContext(QuickJS quickJS) {
                return super.createContext(quickJS);
            }
        };
        eventQueue.start();
    }

    @Test
    public void post() throws InterruptedException {
        eventQueue.post(context -> context.executeVoidScript("var timer = 0;\n" +
                "function print1() {\n" +
                "    console.log(timer++);\n" +
                "    setTimeout(print2, 1000);\n" +
                "}\n" +
                "function print2() {\n" +
                "    console.log(timer++);\n" +
                "    if(timer < 1000){\n" +
                "        setTimeout(print1, 1000);\n" +
                "    }\n" +
                "}\n" +
                "print1();", null));
        eventQueue.quitSafely(20000);
    }

    @Test
    public void quitSafely() throws InterruptedException {
        eventQueue.post(context -> context.executeVoidScript("console.log('Hello')", null));
        eventQueue.quitSafely(1000);
    }

    @Test
    public void quit() {
        eventQueue.post(context -> context.executeVoidScript("console.log('Hello')", null));
        eventQueue.quit();
    }

    @Test
    public void postDelayed() throws InterruptedException {
//        eventQueue.join();
    }
}