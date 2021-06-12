package com.quickjs.plugin;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSFunction;
import com.quickjs.JSObject;
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
        context.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject receiver, JSArray args) {
                workers.add(new Worker((JSFunction) receiver));
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
        private final JSFunction workerReceiver;
        private final QuickJS quickJS;
        private final JSContext context;

        Worker(JSFunction workerReceiver) {
            this.quickJS = QuickJS.createRuntimeWithEventQueue();
            this.context = quickJS.createContext();
            this.context.addPlugin(new ConsolePlugin());
            this.context.addPlugin(new SetTimeoutPlugin());
            this.context.registerJavaMethod(new JavaVoidCallback() {
                @Override
                public void invoke(JSObject receiver, JSArray args) {
                    workerReceiver.executeFunction2("onmessage", args.getString(0));
                }
            }, "postMessage");
            this.workerReceiver = workerReceiver;
            workerReceiver.registerJavaMethod((receiver, args) -> {
                close();
            }, "terminate");
            workerReceiver.registerJavaMethod(new JavaVoidCallback() {
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
