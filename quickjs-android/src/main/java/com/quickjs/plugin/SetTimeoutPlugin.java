package com.quickjs.plugin;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSFunction;
import com.quickjs.Plugin;

public class SetTimeoutPlugin extends Plugin {


    @Override
    public void setup(JSContext context) {
        context.registerJavaMethod((receiver, args) -> {
            JSFunction func = (JSFunction) args.getObject(0);
            long delayMillis = (long) args.getDouble(1);
            new Thread(() -> {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                func.getQuickJS().postEventQueue(() -> {
                    if (!func.getContext().isReleased()) {
                        func.call(null, new JSArray(context));
                    }
                });
            }).start();
        }, "setTimeout");
    }

    @Override
    protected void close(JSContext context) {

    }
}
