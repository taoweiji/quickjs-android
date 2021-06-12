package com.quickjs.plugin;

import com.quickjs.BaseTest;
import com.quickjs.JSContext;
import com.quickjs.QuickJS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkerPluginTest extends BaseTest {
    private JSContext context;
    private QuickJS quickJS;

    @Before
    public void setUp() throws Exception {
        quickJS = QuickJS.createRuntimeWithEventQueue();
        context = quickJS.createContext();
        context.addPlugin(new ConsolePlugin(){
            @Override
            public void println(int priority, String msg) {
                super.println(priority, msg);
            }
        });
        context.addPlugin(new SetTimeoutPlugin());
        context.addPlugin(new WorkerPlugin() {
            @Override
            String getScript(String moduleName) {
                return "var counter = 0;\n" +
                        "function print1() {\n" +
                        "    postMessage(counter++);\n" +
                        "    setTimeout(print1, 10);\n" +
                        "}\n" +
                        "setTimeout(print1, 10);" +
                        "onmessage = function(event) {\n" +
                        "  console.log('Worker ' + event);\n" +
                        "};";
            }

        });
    }

    @After
    public void tearDown() throws Throwable {
        context.close();
        quickJS.close();
    }

    @Test
    public void receiveMessage() throws InterruptedException {
        context.executeVoidScript(" console.log('Received message ' + 1);" +
                "var worker = new Worker('work.js');\n" +
                "worker.onmessage = function (event) {\n" +
                "  if(event == '100'){" +
                "       worker.terminate();" +
                "   }" +
                "   console.log('Received message ' + event);\n" +
                "   worker.postMessage(event);" +
                "};" +
                "", null);
        Thread.sleep(5000);
    }
}