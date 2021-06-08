package com.quickjs.plugin;

import com.quickjs.ES6Module;
import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSObject;
import com.quickjs.JavaVoidCallback;
import com.quickjs.QuickJS;
import com.quickjs.QuickJSExecutor;

public abstract class WorkerPlugin extends QuickJSExecutor {

    public WorkerPlugin(String script, boolean longRunning, String messageHandler) {
        super(script, true, messageHandler);
    }

    public WorkerPlugin(String script) {
        super(script);
    }

    @Override
    protected JSContext createContext(QuickJS quickJS) {
        return new ES6Module(quickJS) {
            @Override
            protected String getModuleScript(String moduleName) {
                return null;
            }
        };
    }

    @Override
    protected void postMessageInner(JSContext context, String[] message) {
        super.postMessageInner(context, message);
    }

    @Override
    protected void setup(JSContext context) {
        super.setup(context);
        context.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject receiver, JSArray args) {
                receiveMessage(args.getString(0));
            }
        }, "postMessage");
    }

    protected abstract void receiveMessage(String msg);
}
