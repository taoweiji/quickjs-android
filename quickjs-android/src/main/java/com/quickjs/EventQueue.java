package com.quickjs;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

class EventQueue implements QuickJSNative {
    private final QuickJS quickJS;
    private final QuickJSNative quickJSNative;
    private final HandlerThread handlerThread;
    private final Handler handler;
    private final Thread thread;
    private final ThreadChecker threadChecker;

    public EventQueue(QuickJS quickJS, HandlerThread handlerThread) {
        this.quickJS = quickJS;
        this.quickJSNative = new QuickJSNativeImpl();
        this.thread = Thread.currentThread();
        this.handlerThread = handlerThread;
        this.handler = Looper.myLooper() != null ? new Handler(Looper.myLooper()) : null;
        this.threadChecker = new ThreadChecker(quickJS);
    }

    public void interrupt() {
        if (handlerThread != null) {
            handlerThread.interrupt();
        }
    }

    public interface Event<T> {
        T run();
    }

    private <T> T post(Event<T> event) {
        if (quickJS.isReleased() || (handlerThread != null && handlerThread.isInterrupted())) {
            Log.e("QuickJS", "QuickJS is released");
            return null;
        }
        if (Thread.currentThread() == thread) {
            return event.run();
        }
        if (handler == null) {
            this.threadChecker.checkThread();
            return event.run();
        }
        Object[] result = new Object[2];
        RuntimeException[] errors = new RuntimeException[1];
        handler.post(() -> {
            try {
                result[0] = event.run();
            } catch (RuntimeException e) {
                errors[0] = e;
            }
            synchronized (result) {
                result[1] = true;
                result.notifyAll();
            }
        });
        synchronized (result) {
            try {
                if (result[1] == null) {
                    result.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (errors[0] != null) {
            throw errors[0];
        }
        return (T) result[0];
    }

    void postVoid(Runnable event) {
        postVoid(event, true);
    }

    void postVoid(Runnable event, boolean block) {
        if (quickJS.isReleased() || (handlerThread != null && handlerThread.isInterrupted())) {
            Log.e("QuickJS", "QuickJS is released");
            return;
        }
        if (Thread.currentThread() == thread) {
            event.run();
            return;
        }
        if (handler == null) {
            this.threadChecker.checkThread();
            event.run();
            return;
        }
        Object[] result = new Object[2];
        RuntimeException[] errors = new RuntimeException[1];
        handler.post(() -> {
            try {
                if (!quickJS.isReleased()) {
                    event.run();
                }
            } catch (RuntimeException e) {
                errors[0] = e;
            }
            if (block) {
                synchronized (result) {
                    result[1] = true;
                    result.notifyAll();
                }
            }
        });
        if (block) {
            synchronized (result) {
                try {
                    if (result[1] == null) {
                        result.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (errors[0] != null) {
                throw errors[0];
            }
        }
    }


    @Override
    public void _releaseRuntime(long runtimePtr) {
        postVoid(() -> quickJSNative._releaseRuntime(runtimePtr));
    }

    @Override
    public long _createContext(long runtimePtr) {
        return quickJSNative._createContext(runtimePtr);
    }

    @Override
    public void _releaseContext(long contextPtr) {
        postVoid(() -> quickJSNative._releaseContext(contextPtr));
    }

    @Override
    public Object _executeScript(long contextPtr, int expectedType, String source, String fileName, int eval_flags) {
        return post(() -> quickJSNative._executeScript(contextPtr, expectedType, source, fileName, eval_flags));
    }

    @Override
    public JSObject _getGlobalObject(long contextPtr) {
        return post(() -> quickJSNative._getGlobalObject(contextPtr));
    }

    @Override
    public void _set(long contextPtr, JSValue objectHandle, String key, Object value) {
        postVoid(() -> quickJSNative._set(contextPtr, objectHandle, key, value));
    }

    @Override
    public Object _get(long contextPtr, int expectedType, JSValue objectHandle, String key) {
        return post(() -> quickJSNative._get(contextPtr, expectedType, objectHandle, key));
    }

    @Override
    public Object _arrayGet(long contextPtr, int expectedType, JSValue objectHandle, int index) {
        return post(() -> quickJSNative._arrayGet(contextPtr, expectedType, objectHandle, index));
    }

    @Override
    public void _arrayAdd(long contextPtr, JSValue objectHandle, Object value) {
        postVoid(() -> quickJSNative._arrayAdd(contextPtr, objectHandle, value));
    }

    @Override
    public Object _executeFunction(long contextPtr, int expectedType, JSValue objectHandle, String name, JSValue parametersHandle) {
        return post(() -> quickJSNative._executeFunction(contextPtr, expectedType, objectHandle, name, parametersHandle));
    }

    @Override
    public Object _executeFunction2(long contextPtr, int expectedType, JSValue objectHandle, JSValue functionHandle, JSValue parametersHandle) {
        return post(() -> quickJSNative._executeFunction2(contextPtr, expectedType, objectHandle, functionHandle, parametersHandle));
    }

    @Override
    public JSObject _initNewJSObject(long contextPtr) {
        return post(() -> quickJSNative._initNewJSObject(contextPtr));
    }

    @Override
    public JSArray _initNewJSArray(long contextPtr) {
        return post(() -> quickJSNative._initNewJSArray(contextPtr));
    }

    @Override
    public JSFunction _initNewJSFunction(long contextPtr, int javaCallerId, boolean voidMethod) {
        return post(() -> quickJSNative._initNewJSFunction(contextPtr, javaCallerId, voidMethod));
    }

    @Override
    public void _releasePtr(long contextPtr, long tag, int u_int32, double u_float64, long u_ptr) {
        postVoid(() -> quickJSNative._releasePtr(contextPtr, tag, u_int32, u_float64, u_ptr));
    }

    @Override
    public JSFunction _registerJavaMethod(long contextPtr, JSValue objectHandle, String jsFunctionName, int javaCallerId, boolean voidMethod) {
        return post(() -> quickJSNative._registerJavaMethod(contextPtr, objectHandle, jsFunctionName, javaCallerId, voidMethod));
    }

    @Override
    public int _getObjectType(long contextPtr, JSValue objectHandle) {
        return post(() -> quickJSNative._getObjectType(contextPtr, objectHandle));
    }

    @Override
    public boolean _contains(long contextPtr, JSValue objectHandle, String key) {
        return post(() -> quickJSNative._contains(contextPtr, objectHandle, key));
    }

    @Override
    public String[] _getKeys(long contextPtr, JSValue objectHandle) {
        return post(() -> quickJSNative._getKeys(contextPtr, objectHandle));
    }

    @Override
    public boolean _isUndefined(long contextPtr, JSValue value) {
        return post(() -> quickJSNative._isUndefined(contextPtr, value));
    }

    @Override
    public JSValue _Undefined(long contextPtr) {
        return post(() -> quickJSNative._Undefined(contextPtr));
    }

    @Override
    public JSValue _getValue(long contextPtr, JSObject object, String key) {
        return post(() -> quickJSNative._getValue(contextPtr, object, key));
    }

    @Override
    public JSValue _arrayGetValue(long contextPtr, JSArray array, int index) {
        return post(() -> quickJSNative._arrayGetValue(contextPtr, array, index));
    }

    @Override
    public String[] _getException(long contextPtr) {
        return post(() -> quickJSNative._getException(contextPtr));
    }

    @Override
    public JSFunction _newClass(long contextPtr, int javaCallerId) {
        return post(() -> quickJSNative._newClass(contextPtr, javaCallerId));
    }
}
