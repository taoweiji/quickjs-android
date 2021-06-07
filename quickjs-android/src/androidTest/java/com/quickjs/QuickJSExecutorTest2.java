//package com.quickjs;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//
//public class QuickJSExecutorTest2 {
//    private JSContext context;
//    private QuickJS quickJS;
//    private final String workerScript = "var worker = new Object();\n" +
//            "worker.callbacks = [];\n" +
//            "worker.onMessage = function (callback) {\n" +
//            "    worker.callbacks.push(callback)\n" +
//            "}\n" +
//            "worker.postMessage = function (params) {\n" +
//            "    for (var i = 0; i < worker.callbacks.length; i++) {\n" +
//            "        worker.callbacks[i](params);\n" +
//            "    }\n" +
//            "}";
//
//    @Before
//    public void setUp() throws Exception {
//        quickJS = QuickJS.createRuntime();
//        context = quickJS.createContext();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        context.close();
//        quickJS.close();
//    }
//
//    @Test
//    public void postMessage1() {
//        context.executeVoidScript(workerScript, null);
//        JSObject worker = context.getObject("worker");
//        JSFunction onMessage = (JSFunction) worker.getObject("onMessage");
//        Object[] data = new Object[1];
//        JSFunction event = new JSFunction(context, new JavaVoidCallback() {
//            @Override
//            public void invoke(JSObject receiver, JSArray args) {
//                data[0] = args.getString(0);
//            }
//        });
//        onMessage.call(worker, new JSArray(context).push(event));
////        context.executeVoidScript("worker.postMessage('Hello')", null);
//        worker.executeFunction2("postMessage", "Hello");
//        assertEquals("Hello", data[0]);
//    }
//
//
//    @Test
//    public void postMessage2() {
//        context.executeVoidScript(workerScript, null);
//        JSObject worker = context.getObject("worker");
//        Object[] data = new Object[1];
//        context.registerJavaMethod(new JavaVoidCallback() {
//            @Override
//            public void invoke(JSObject receiver, JSArray args) {
//                data[0] = args.getString(0);
//            }
//        }, "log");
//        context.executeVoidScript("worker.onMessage(function(params) {\n" +
//                "    log(params)\n" +
//                "})", null);
//        worker.executeFunction2("postMessage", "Hello");
//        assertEquals("Hello", data[0]);
//    }
//
//
//}