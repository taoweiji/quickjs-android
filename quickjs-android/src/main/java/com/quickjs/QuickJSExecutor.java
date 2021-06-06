package com.quickjs;

import java.util.LinkedList;

public class QuickJSExecutor extends Thread {

    private final String script;
    private JSContext runtime;
    private String result;
    private volatile boolean terminated = false;
    private volatile boolean shuttingDown = false;
    private volatile boolean forceTerminating = false;
    private Exception exception = null;
    private final LinkedList<String[]> messageQueue = new LinkedList<>();
    private final boolean longRunning;
    private final String messageHandler;
    private QuickJS quickJS;

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

    /**
     * Override to provide a custom setup for this V8 runtime.
     * This method can be overridden to configure the V8 runtime,
     * for example, to add callbacks or to add some additional
     * functionality to the global scope.
     *
     * @param runtime The runtime to configure.
     */
    protected void setup(final JSContext runtime) {

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

    /**
     * Posts a message to the receiver to be processed by the executor
     * and sent to the V8 runtime via the messageHandler.
     *
     * @param message The message to send to the messageHandler
     */
    public void postMessage(final String... message) {
        synchronized (this) {
            messageQueue.add(message);
            notify();
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        synchronized (this) {
            this.quickJS = QuickJS.createRuntime();
            runtime = quickJS.createContext();
            runtime.registerJavaMethod(new ExecutorTermination(), "__j2v8__checkThreadTerminate");
            setup(runtime);
        }
        try {
            if (!forceTerminating) {
                Object scriptResult = runtime.executeScript(script, getName());
                if (scriptResult != null) {
                    result = scriptResult.toString();
                }
            }
            while (!forceTerminating && longRunning) {
                synchronized (this) {
                    if (messageQueue.isEmpty() && !shuttingDown) {
                        wait();
                    }
                    if ((messageQueue.isEmpty() && shuttingDown) || forceTerminating) {
                        return;
                    }
                }
                if (!messageQueue.isEmpty()) {
                    String[] message = messageQueue.remove(0);
                    JSArray parameters = new JSArray(runtime);
                    JSArray strings = new JSArray(runtime);
                    for (String string : message) {
                        strings.push(string);
                    }
                    parameters.push(strings);
                    runtime.executeVoidFunction(messageHandler, parameters);
                }
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            synchronized (this) {
//                if (runtime.getLocker().hasLock()) {
                runtime.close();
                quickJS.createContext();
                runtime = null;
                quickJS = null;
//                }
                terminated = true;
            }
        }
    }

    /**
     * Determines if an exception was thrown during the JavaScript execution.
     *
     * @return True if an exception was thrown during the JavaScript execution,
     * false otherwise.
     */
    public boolean hasException() {
        return exception != null;
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
    public boolean hasTerminated() {
        return terminated;
    }

    /**
     * Forces the executor to shutdown immediately. Any currently executing
     * JavaScript will be interrupted and all outstanding messages will be
     * ignored.
     */
    public void forceTermination() {
        synchronized (this) {
            forceTerminating = true;
            shuttingDown = true;
            if (runtime != null) {
                runtime.close();
                quickJS.createContext();
            }
            notify();
        }
    }

    /**
     * Indicates to the executor that it should shutdown. Any currently
     * executing JavaScript will be allowed to finish, and any outstanding
     * messages will be processed. Only once the message queue is empty,
     * will the executor actually shtutdown.
     */
    public void shutdown() {
        synchronized (this) {
            shuttingDown = true;
            notify();
        }
    }

    /**
     * Returns true if shutdown() or forceTermination() was called to
     * shutdown this executor.
     *
     * @return True if shutdown() or forceTermination() was called, false otherwise.
     */
    public boolean isShuttingDown() {
        return shuttingDown;
    }

    /**
     * Returns true if forceTermination was called to shutdown
     * this executor.
     *
     * @return True if forceTermination() was called, false otherwise.
     */
    public boolean isTerminating() {
        return forceTerminating;
    }

    class ExecutorTermination implements JavaVoidCallback {
        @Override
        public void invoke(final JSObject receiver, final JSArray parameters) {
            if (forceTerminating) {
                throw new RuntimeException("V8Thread Termination");
            }
        }
    }
}