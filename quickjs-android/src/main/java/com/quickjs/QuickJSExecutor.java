package com.quickjs;

import java.util.LinkedList;

public class QuickJSExecutor extends Thread {

    protected final String script;
    protected JSContext context;
    protected String result;
    protected volatile boolean terminated = false;
    protected volatile boolean softClose = false;
    protected Exception exception = null;
    protected final LinkedList<String[]> messageQueue = new LinkedList<>();
    protected final boolean longRunning;
    protected String messageHandler;
    protected QuickJS quickJS;

    /**
     * Create a new executor and execute the given script on it. Once
     * the script has finished executing, the executor can optionally
     * wait on a message queue.
     *
     * @param script         The script to execute on this executor.
     * @param longRunning    True to indicate that this executor should be longRunning.
     * @param messageHandler The name of the message handler that should be notified
     *                       when messages are delivered.
     */
    public QuickJSExecutor(final String script, final boolean longRunning, final String messageHandler) {
        this.script = script;
        this.longRunning = longRunning;
        this.messageHandler = messageHandler;
    }

    /**
     * Create a new executor and execute the given script on it.
     *
     * @param script The script to execute on this executor.
     */
    public QuickJSExecutor(final String script) {
        this(script, false, null);
    }


    protected void setup(final JSContext context) {

    }

    protected JSContext createContext(QuickJS quickJS) {
        return quickJS.createContext();
    }

    /**
     * Gets the result of the JavaScript that was executed
     * on this executor.
     *
     * @return The result of the JS Script that was executed on
     * this executor.
     */
    public String getResult() {
        return result;
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        synchronized (this) {
            this.quickJS = QuickJS.createRuntime();
            context = createContext(quickJS);
            setup(context);
        }
        try {
            if (!isInterrupted()) {
                Object scriptResult = context.executeScript(script, getName());
                if (scriptResult != null) {
                    result = scriptResult.toString();
                }
            }
            while (!isInterrupted() && longRunning) {
                synchronized (this) {
                    if (messageQueue.isEmpty() && !softClose) {
                        wait();
                    }
                    if ((messageQueue.isEmpty() && softClose) || isInterrupted()) {
                        break;
                    }
                }
                if (!messageQueue.isEmpty()) {
                    String[] message = messageQueue.remove(0);
                    postMessageInner(context, message);
                }
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            synchronized (this) {
                context.close();
                quickJS.createContext();
                context = null;
                quickJS = null;
                terminated = true;
            }
        }
    }

    /**
     * Posts a message to the receiver to be processed by the executor
     * and sent to the runtime via the messageHandler.
     *
     * @param message The message to send to the messageHandler
     */
    public void postMessage(final String... message) {
        synchronized (this) {
            messageQueue.add(message);
            notify();
        }
    }

    protected void postMessageInner(JSContext context, String[] message) {
        JSArray parameters = new JSArray(context);
        for (String string : message) {
            parameters.push(string);
        }
        context.executeVoidFunction(messageHandler, parameters);
    }

    /**
     * Gets the exception that was thrown during the JavaScript execution.
     *
     * @return The exception that was thrown during the JavaScript execution,
     * or null if no such exception was thrown.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Determines if the executor has terminated.
     *
     * @return True if the executor has terminated, false otherwise.
     */
    public boolean isTerminated() {
        return terminated;
    }


    @Override
    public void interrupt() {
        super.interrupt();
        synchronized (this) {
            notify();
        }
    }

    /**
     * Indicates to the executor that it should shutdown. Any currently
     * executing JavaScript will be allowed to finish, and any outstanding
     * messages will be processed. Only once the message queue is empty,
     * will the executor actually softClose.
     */
    public void softClose() {
        synchronized (this) {
            softClose = true;
            notify();
        }
    }

    public boolean isSoftClose() {
        return softClose;
    }

}