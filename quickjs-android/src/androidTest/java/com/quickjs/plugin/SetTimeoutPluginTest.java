//package com.quickjs.plugin;
//
//import com.quickjs.JSContext;
//import com.quickjs.QuickJS;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//public class SetTimeoutPluginTest {
//    private JSContext context;
//    private QuickJS quickJS;
//    SetTimeoutPlugin setTimeoutPlugin = new SetTimeoutPlugin();
//
//    @Before
//    public void setUp() throws Throwable {
//        setTimeoutPlugin.post(new Runnable() {
//            @Override
//            public void run() {
//                quickJS = QuickJS.createRuntime();
//                context = quickJS.createContext();
//                context.addPlugin(setTimeoutPlugin);
//                context.addPlugin(new ConsolePlugin());
//            }
//        });
//    }
//
//    @After
//    public void tearDown() throws Throwable {
//        context.close();
//        quickJS.close();
//    }
//
////    @Test
//    public void test() throws Exception {
//        setTimeoutPlugin.post(new Runnable() {
//            @Override
//            public void run() {
//                context.executeVoidScript(
//                        "var timer = 0;\n" +
//                                "function print1() {\n" +
//                                "    console.log(timer++);\n" +
//                                "    setTimeout(print2, 1000);\n" +
//                                "}\n" +
//                                "function print2() {\n" +
//                                "    console.log(timer++);\n" +
//                                "    if(timer < 1000){\n" +
//                                "        setTimeout(print1, 1000);\n" +
//                                "    }\n" +
//                                "}\n" +
//                                "print1();", null);
//            }
//        });
//        setTimeoutPlugin.getHandlerThread().join();
//    }
//}