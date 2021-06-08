//package com.quickjs.plugin;
//
//import android.os.Handler;
//import android.os.HandlerThread;
//
//import com.quickjs.JSArray;
//import com.quickjs.JSContext;
//import com.quickjs.JSFunction;
//import com.quickjs.Plugin;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class SetTimeoutPlugin extends Plugin {
//    private HandlerThread handlerThread;
//
//    public SetTimeoutPlugin() {
//        handlerThread = new HandlerThread("SetTimeoutPlugin");
//        handlerThread.start();
//    }
//
//    @Override
//    public void setup(JSContext context) {
//        context.registerJavaMethod((receiver, args) -> {
//            JSFunction func = (JSFunction) args.getObject(0);
//            int timer = args.getInteger(1);
//            postDelayed(() -> func.call(null, new JSArray(context)), timer);
//        }, "setTimeout");
//    }
//
//    @Override
//    protected void close(JSContext context) {
//
//    }
//
//    public final boolean postDelayed(Runnable runnable, long delayMillis) {
//        return new Handler(handlerThread.getLooper()).postDelayed(runnable, delayMillis);
//    }
//
//    public boolean post(Runnable runnable) {
//        return new Handler(handlerThread.getLooper()).post(runnable);
//    }
//
//    public HandlerThread getHandlerThread() {
//        return handlerThread;
//    }
//}
