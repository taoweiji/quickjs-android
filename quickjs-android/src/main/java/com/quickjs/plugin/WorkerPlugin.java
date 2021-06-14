package com.quickjs.plugin;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSFunction;
import com.quickjs.JSObject;
import com.quickjs.JavaCallback;
import com.quickjs.JavaConstructorCallback;
import com.quickjs.JavaVoidCallback;
import com.quickjs.Plugin;
import com.quickjs.QuickJS;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http://www.ruanyifeng.com/blog/2018/07/web-worker.html
 */
public abstract class WorkerPlugin extends Plugin {
    private Map<Long, Worker> workers = new HashMap<>();

    @Override
    protected void setup(JSContext context) {
        context.registerClass((thisObj, args) -> {
            String url = args.getString(0);
            workers.put(thisObj.getTag(), new Worker(WorkerPlugin.this, thisObj, url));
        }, "Worker");
    }

    @Override
    protected void close(JSContext context) {
        for (Worker worker : workers.values()) {
            worker.close();
        }
    }

    abstract String getScript(String moduleName);


    static class Worker implements Closeable {
        private final JSObject workerObj;
        private final QuickJS quickJS;
        private final JSContext context;

        Worker(WorkerPlugin workerPlugin, JSObject workerObj, String url) {
            this.quickJS = QuickJS.createRuntimeWithEventQueue();
            this.context = quickJS.createContext();
            this.context.addPlugin(new ConsolePlugin());
            this.context.addPlugin(new SetTimeoutPlugin());
            this.context.registerJavaMethod(new JavaVoidCallback() {
                @Override
                public void invoke(JSObject receiver, JSArray args) {
                    String event = args.getString(0);
                    sendMessageReceiver(event);
                }
            }, "postMessage");
            this.workerObj = workerObj;
            initWorkerReceiver();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    context.executeVoidScript(workerPlugin.getScript(url), url);
                }
            }).start();
        }

        private void sendMessageReceiver(String event) {
            workerObj.postEventQueue(() -> {
                JSObject onmessage = workerObj.getObject("onmessage");
                if (onmessage instanceof JSFunction) {
                    JSFunction func = (JSFunction) onmessage;
                    func.call(workerObj, new JSArray(workerObj.getContext()).push(event));
                }
            });
        }

        private void initWorkerReceiver() {
            workerObj.registerJavaMethod((receiver, args) -> {
                close();
            }, "terminate");
            workerObj.registerJavaMethod(new JavaVoidCallback() {
                @Override
                public void invoke(JSObject receiver, JSArray args) {
                    postMessage(args.getString(0));
                }
            }, "postMessage");
        }

        private boolean terminate;

        @Override
        public void close() {
            if (terminate) {
                return;
            }
            terminate = true;
            quickJS.postEventQueue(quickJS::close);
        }

        public void postMessage(String msg) {
            quickJS.postEventQueue(() -> {
                JSObject func = context.getObject("onmessage");
                if (func != null && !func.isUndefined()) {
                    ((JSFunction) func).call(null, new JSArray(context).push(msg));
                }
            });
        }
    }
}
