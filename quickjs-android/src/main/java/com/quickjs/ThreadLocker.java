package com.quickjs;

/**
 * 线程锁
 */
public class ThreadLocker {
    private Thread thread;
    private boolean released;

    private QuickJS runtime;

    public ThreadLocker(QuickJS runtime) {
        this.runtime = runtime;
        this.acquire();
    }

    public synchronized void acquire() {
        if (this.thread != null && this.thread != Thread.currentThread()) {
            throw new Error("Invalid QuickJS thread access: current thread is " + Thread.currentThread() + " while the locker has thread " + this.thread);
        } else if (this.thread != Thread.currentThread()) {
//            this.runtime.acquireLock(this.runtime.getV8RuntimePtr());
            this.thread = Thread.currentThread();
            this.released = false;
        }
    }

    public void checkThread() {
        if (this.released && this.thread == null) {
            throw new Error("Invalid QuickJS thread access: the locker has been released!");
        } else if (this.thread != Thread.currentThread()) {
            throw new Error("Invalid QuickJS thread access: current thread is " + Thread.currentThread() + " while the locker has thread " + this.thread);
        }
    }
}
