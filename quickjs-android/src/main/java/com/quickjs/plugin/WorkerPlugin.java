package com.quickjs.plugin;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSFunction;
import com.quickjs.JSObject;
import com.quickjs.JavaCallback;
import com.quickjs.JavaVoidCallback;
import com.quickjs.Plugin;
import com.quickjs.QuickJS;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * http://www.ruanyifeng.com/blog/2018/07/web-worker.html
 */
public abstract class WorkerPlugin extends Plugin {
    private List<Worker> workers = new ArrayList<>();

    @Override
    protected void setup(JSContext context) {
        context.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject receiver, JSArray args) {
//                receiveMessage(args.getString(0));
            }
        }, "postMessage");
        context.registerClass(new JavaCallback() {
            @Override
            public Object invoke(JSObject receiver, JSArray args) {
                JSObject workerObj = new JSObject(context);
                String url = args.getString(0);
                workers.add(new Worker(WorkerPlugin.this, workerObj, url));
                return workerObj;
            }
        }, "Worker");
    }

    @Override
    protected void close(JSContext context) {
        for (Worker worker : workers) {
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
                    workerObj.executeFunction("onmessage", new JSArray(workerObj.getContext()).push(event));
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

        private void initWorkerReceiver() {
            workerObj.registerJavaMethod((receiver, args) -> {
                close();
            }, "terminate");
            workerObj.registerJavaMethod(new JavaVoidCallback() {
                @Override
                public void invoke(JSObject receiver, JSArray args) {

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
            quickJS.close();
        }
    }
}
