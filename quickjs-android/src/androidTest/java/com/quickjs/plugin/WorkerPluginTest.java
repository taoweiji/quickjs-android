package com.quickjs.plugin;

import android.util.Log;

import com.quickjs.JSContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkerPluginTest {

    WorkerPlugin plugin;

    @Before
    public void setUp() throws Exception {
        plugin = new WorkerPlugin(null) {
            @Override
            protected void setup(JSContext context) {
                super.setup(context);
                context.addPlugin(new ConsolePlugin() {
                    @Override
                    public void println(int priority, String msg) {
                        super.println(priority, msg);
                    }
                });
                context.executeVoidScript(
                        "var i = 0;\n" +
                                "function timedCount() {\n" +
                                "    i = i + 1;\n" +
                                "    postMessage(i);\n" +
                                "    console.log(i);\n" +
                                "    setTimeout(\"timedCount()\", 500);\n" +
                                "}\n" +
                                "timedCount();", null);
            }

            @Override
            protected void receiveMessage(String msg) {
                Log.e("receiveMessage", msg);
            }
        };
        plugin.start();
    }

    @After
    public void tearDown() throws Exception {
//        plugin.join();
    }

    @Test
    public void postMessage() {

    }

    @Test
    public void setup() {

    }
}