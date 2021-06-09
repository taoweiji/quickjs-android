package com.quickjs;

import com.quickjs.plugin.ConsolePlugin;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EventQueueTest {
    EventQueue<ES6Module> eventQueue;
    List<String> logs = new ArrayList<>();

    @Before
    public void setUp() {
        eventQueue = new EventQueue<ES6Module>() {
            @Override
            protected void setup(ES6Module context) {
                super.setup(context);
                context.addPlugin(new ConsolePlugin() {
                    @Override
                    public void println(int priority, String msg) {
                        super.println(priority, msg);
                        logs.add(msg);
                    }
                });
            }

            @Override
            protected ES6Module createContext(QuickJS quickJS) {
                return new ES6Module(quickJS) {

                    @Override
                    protected String getModuleScript(String moduleName) {
                        return null;
                    }
                };
            }
        };
        eventQueue.start();
    }

    @Test
    public void post() throws InterruptedException {
        eventQueue.post(new EventQueue.Event<ES6Module>() {
            @Override
            public void on(ES6Module context) {
                context.executeGlobalScript("var counter = 0;\n" +
                        "function print1() {\n" +
                        "    console.log(counter++);\n" +
                        "    setTimeout(print1, 10);\n" +
                        "}\n" +
                        "print1();", null);
            }
        });
        eventQueue.join(1000);
        eventQueue.quit();
        assertTrue(logs.size() > 10);
    }

    @Test
    public void quitSafely() throws InterruptedException {
        eventQueue.post(new EventQueue.Event<ES6Module>() {
            @Override
            public void on(ES6Module context) {
                context.executeGlobalScript("console.log('Hello')", null);
            }
        });
        eventQueue.quitSafely();
        assertEquals(1, logs.size());
    }

    @Test
    public void quit() {
        eventQueue.post(new EventQueue.Event<ES6Module>() {
            @Override
            public void on(ES6Module context) {
                context.executeGlobalScript("console.log('Hello')", null);
            }
        });
        eventQueue.quit();
        assertEquals(0, logs.size());
    }

    @Test
    public void postDelayed() throws InterruptedException {
        eventQueue.postDelayed(new EventQueue.Event<ES6Module>() {
            @Override
            public void on(ES6Module context) {
                context.executeGlobalScript("console.log('Hello')", null);
            }
        }, 1000);
        eventQueue.quitSafely();
        assertEquals(0, logs.size());
    }
}