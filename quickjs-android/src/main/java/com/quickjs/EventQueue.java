package com.quickjs;

import android.os.Handler;
import android.os.HandlerThread;

public abstract class EventQueue<T extends JSContext> {
    protected final HandlerThread handlerThread;
    protected final boolean enableSetTimeout;
    protected QuickJS quickJS;
    protected T context;

    public EventQueue() {
        this(true);
    }

    public EventQueue(boolean enableSetTimeout) {
        this.enableSetTimeout = enableSetTimeout;
        this.handlerThread = new HandlerThread("EventQueue");
    }

    public void post(Event<T> event) {
        if (quited) {
            return;
        }
        new Handler(this.handlerThread.getLooper()).post(() -> event.on(context));
    }

    public void postDelayed(Event<T> event, long delayMillis) {
        if (quited) {
            return;
        }
        new Handler(this.handlerThread.getLooper()).postDelayed(() -> event.on(context), delayMillis);
    }

    protected abstract T createContext(QuickJS quickJS);

    protected void setup(T context) {

    }

    public void join() throws InterruptedException {
        this.join(0);
    }

    public void join(int millis) throws InterruptedException {
        handlerThread.join(millis);
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

    private void registerSetTimeout(T context) {
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
        quited = true;
        // 等待event全部消费
        handlerThread.quitSafely();
        handlerThread.join(timeout);
        if (timeout > 0) {
            handlerThread.interrupt();
        }
    }

    private boolean quited = false;

    public void quitSafely() throws InterruptedException {
        quitSafely(0);
    }

    /**
     * 队列中的任务不会再执行，直接终止
     */
    public void quit() {
        quited = true;
        handlerThread.interrupt();
    }

    public interface Event<T> {
        void on(T context);
    }
}
