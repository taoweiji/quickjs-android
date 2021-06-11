package com.quickjs;

import com.quickjs.plugin.ConsolePlugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuickJSExecutorTest  extends BaseTest{
    QuickJSExecutor executor;
    List<String> logs = new ArrayList<>();

    @Before
    public void setUp() {
        executor = new QuickJSExecutor("function postMessage(msg) {console.log(msg.toString())}", true, "postMessage") {
            @Override
            protected void setup(JSContext context) {
                context.addPlugin(new ConsolePlugin() {
                    @Override
                    public void println(int priority, String msg) {
                        super.println(priority, msg);
                        logs.add(msg);
                    }
                });
            }
        };
    }

    @After
    public void tearDown() {
        executor.interrupt();
    }

    @Test
    public void softClose() throws InterruptedException {
        executor.start();
        executor.postMessage("Hello");
        executor.postMessage("World");
        executor.softClose();
        executor.join();
        assertEquals("Hello", logs.get(0));
        assertEquals("World", logs.get(1));
        assertTrue(executor.isTerminated());
    }

    @Test
    public void interrupt() throws InterruptedException {
        executor.start();
        executor.postMessage("Hello");
        executor.postMessage("World");
        executor.interrupt();
        executor.join(1000);
        assertTrue(logs.isEmpty());
    }
}