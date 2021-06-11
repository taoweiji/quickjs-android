package com.quickjs.plugin;

import com.quickjs.BaseTest;
import com.quickjs.JSContext;
import com.quickjs.QuickJS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConsolePluginTest  extends BaseTest {

    private JSContext context;
    private QuickJS quickJS;
    private Object result;

    @Before
    public void setUp() throws Throwable {
        quickJS = createQuickJS();
        context = quickJS.createContext();
        context.addPlugin(new ConsolePlugin() {
            @Override
            public void println(int priority, String msg) {
                result = msg;
                super.println(priority, msg);
            }
        });
    }

    @After
    public void tearDown() throws Throwable {
        context.close();
        quickJS.close();
    }


    @Test
    public void log() {
        context.executeVoidScript("console.log('log')", null);
        assertEquals(result, "log");
    }

    @Test
    public void info() {
        context.executeVoidScript("console.info('info')", null);
        assertEquals(result, "info");
    }

    @Test
    public void error() {
        context.executeVoidScript("console.error('error')", null);
        assertEquals(result, "error");
    }

    @Test
    public void warn() {
        context.executeVoidScript("console.warn('warn')", null);
        assertEquals(result, "warn");
    }

    @Test
    public void count() {
        context.executeVoidScript("console.log('log')", null);
        context.executeVoidScript("console.info('info')", null);
        context.executeVoidScript("console.error('error')", null);
        context.executeVoidScript("console.warn('warn')", null);
        assertEquals(4, context.executeIntegerScript("console.count()", null));
    }

    @Test
    public void clear() {
    }

    @Test
    public void group() {
    }

    @Test
    public void groupCollapsed() {
    }

    @Test
    public void groupEnd() {
    }

    @Test
    public void table() {
        context.executeVoidScript("var arr= [ \n" +
                "         { num: \"1\"},\n" +
                "         { num: \"2\"}, \n" +
                "         { num: \"3\" }\n" +
                "    ];\n" +
                "console.table(arr);\n" +
                "\n" +
                "var obj= {\n" +
                "     a:{ num: \"1\"},\n" +
                "     b:{ num: \"2\"},\n" +
                "     c:{ num: \"3\" }\n" +
                "};\n" +
                "console.table(obj);", null);
        assertEquals("{\"a\":{\"num\":\"1\"},\"b\":{\"num\":\"2\"},\"c\":{\"num\":\"3\"}}", result);
    }

    @Test
    public void time() {
        context.executeVoidScript("console.time('计时器1222');\n" +
                "console.time('计时器1');\n" +
                "console.time('计时器1');\n" +
                "for (var i = 0; i < 100; i++) {\n" +
                "  for (var j = 0; j < 100; j++) {}\n" +
                "}\n" +
                "console.timeEnd('计时器1');\n" +
                "console.time('计时器2');\n" +
                "for (var i = 0; i < 1000; i++) {\n" +
                "  for (var j = 0; j < 10; j++) {}\n" +
                "}\n" +
                "console.timeEnd('计时器2');", null);

    }

    @Test
    public void timeEnd() {

    }

    @Test
    public void trace() {
    }
}