package com.quickjs;

import android.os.Handler;
import android.os.HandlerThread;

public class EventQueue {
    private final HandlerThread handlerThread;
    private final boolean enableSetTimeout;
    private QuickJS quickJS;
    private JSContext context;

    public EventQueue() {
        this(true);
    }

    public EventQueue(boolean enableSetTimeout) {
        this.enableSetTimeout = enableSetTimeout;
        this.handlerThread = new HandlerThread("EventQueue");
    }

    public void post(Event event) {
        new Handler(this.handlerThread.getLooper()).post(() -> event.on(context));
    }

    public void postDelayed(Event event, long delayMillis) {
        new Handler(this.handlerThread.getLooper()).postDelayed(() -> event.on(context), delayMillis);
    }

    protected JSContext createContext(QuickJS quickJS) {
        return quickJS.createContext();
    }

    protected void setup(JSContext context) {

    }

    public void join() throws InterruptedException {
        handlerThread.join();
    }

    public void start() {
        this.handlerThread.start();
        new Handler(this.handlerThread.getLooper()).post(() -> {
            if (quickJS == null) {
                quickJS = QuickJS.createRuntime();
                context = createContext(quickJS);
                setup(context);
                if (enableSetTimeout) {
                    registerSetTimeout(context);
                }
            }
        });
    }

    private void registerSetTimeout(JSContext context) {
        context.registerJavaMethod((receiver, args) -> {
            JSFunction func = (JSFunction) args.getObject(0);
            int delayMillis = args.getInteger(1);
            postDelayed(ctx -> func.call(null, new JSArray(ctx)), delayMillis);
        }, "setTimeout");
    }

    /**
     * 如果当前有事件在队列中就不会退出，但是也不会接受新的事件，直到事件全部结果，超时也会直接结束
     */
    public void quitSafely(int timeout) throws InterruptedException {
        // 等待event全部消费
        handlerThread.join(timeout);
        if (timeout > 0) {
            handlerThread.interrupt();
        }
    }

    public void quitSafely() throws InterruptedException {
        quitSafely(0);
    }

    /**
     * 队列中的任务不会再执行，直接终止
     */
    public void quit() {
        handlerThread.interrupt();
    }

    public interface Event {
        void on(JSContext context);
    }
}
