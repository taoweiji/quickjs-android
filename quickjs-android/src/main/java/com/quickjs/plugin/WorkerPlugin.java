package com.quickjs.plugin;

import com.quickjs.JSContext;
import com.quickjs.QuickJS;

import java.util.LinkedList;

public class WorkerPlugin extends Thread {
    private final LinkedList<String> messageQueue = new LinkedList<>();
    private QuickJS quickJS;
    private JSContext context;
    private final String messageHandler;
    private final String script;
    private final boolean longRunning;

    public WorkerPlugin(String script, boolean longRunning, String messageHandler) {
        this.script = script;
        this.longRunning = longRunning;
        this.messageHandler = messageHandler;
    }

    public WorkerPlugin(final String script) {
        this(script, false, null);
    }


    public void postMessage(String message) {
        synchronized (this) {
            messageQueue.add(message);
            notify();
        }
    }

    protected void setup(final JSContext context) {

    }

    protected JSContext createContext(QuickJS quickJS) {
        return quickJS.createContext();
    }

    @Override
    public void run() {
        synchronized (this) {
            this.quickJS = QuickJS.createRuntime();
            this.context = createContext(quickJS);
            setup(this.context);
        }
        try {
            if (script != null) {
                this.context.executeScript(script, null);
            }
            while (!isInterrupted() && longRunning) {
                synchronized (this) {
                    if (messageQueue.isEmpty()) {
                        wait();
                    }
                }
                if (isInterrupted()) {
                    return;
                }
                if (!messageQueue.isEmpty()) {
                    String message = messageQueue.remove(0);
                    context.executeFunction2(messageHandler, message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
            quickJS.close();
        }
    }
}
