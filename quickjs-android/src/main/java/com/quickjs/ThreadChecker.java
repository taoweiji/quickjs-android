package com.quickjs;

/**
 * 线程锁
 */
class ThreadChecker {
    private Thread thread;
    private boolean released;

    private QuickJS runtime;

    public ThreadChecker(QuickJS runtime) {
        this.runtime = runtime;
        this.acquire();
    }

    public synchronized void acquire() {
        if (this.thread != null && this.thread != Thread.currentThread()) {
            throw new Error("All QuickJS methods must be called on the same thread. Invalid QuickJS thread access: current thread is " + Thread.currentThread() + " while the locker has thread " + this.thread);
        } else if (this.thread != Thread.currentThread()) {
            this.thread = Thread.currentThread();
            this.released = false;
        }
    }

    public void checkThread() {
        if (this.released && this.thread == null) {
            throw new Error("Invalid QuickJS thread access: the locker has been released!");
        } else if (this.thread != Thread.currentThread()) {
            throw new Error("All QuickJS methods must be called on the same thread. Invalid QuickJS thread access: current thread is " + Thread.currentThread() + " while the locker has thread " + this.thread);
        }
    }
}
