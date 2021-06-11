package com.quickjs;

import android.util.Log;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JSContext extends JSObject implements Closeable {
    final QuickJS quickJS;
    private final long contextPtr;
    Map<Integer, QuickJS.MethodDescriptor> functionRegistry = new HashMap<>();
    final LinkedList<WeakReference<JSValue>> refs = new LinkedList<>();
    Set<Plugin> plugins = new HashSet<>();

    JSContext(QuickJS quickJS, long contextPtr) {
        super(null, QuickJS._getGlobalObject(contextPtr));
        this.quickJS = quickJS;
        this.contextPtr = contextPtr;
        this.context = this;
        QuickJS.sContextMap.put(contextPtr, this);
    }

    long getContextPtr() {
        return this.contextPtr;
    }

    void addObjRef(JSValue reference) {
        if (reference.getClass() != JSValue.class) {
            refs.add(new WeakReference<>(reference));
        }
    }

    private final List<Object[]> releaseObjPtrPool = Collections.synchronizedList(new LinkedList<>());

    void releaseObjRef(JSValue reference, boolean finalize) {
        if (finalize) {
            releaseObjPtrPool.add(new Object[]{reference.tag, reference.u_int32, reference.u_float64, reference.u_ptr});
        } else {
            QuickJS._release(getContextPtr(), reference);
        }
        removeObjRef(reference);
    }

    private void checkReleaseObjPtrPool() {
        while (!releaseObjPtrPool.isEmpty()) {
            Object[] ptr = releaseObjPtrPool.get(0);
            QuickJS._releasePtr(getContextPtr(), (long) ptr[0], (int) ptr[1], (double) ptr[2], (long) ptr[3]);
            releaseObjPtrPool.remove(0);
        }
    }

    void removeObjRef(JSValue reference) {
        for (WeakReference<JSValue> it : refs) {
            JSValue tmp = it.get();
            if (tmp == reference) {
                refs.remove(it);
                return;
            }
        }
    }


    @Override
    public void close() {
        if (released) {
            return;
        }
        for (Plugin plugin : plugins) {
            plugin.close(this);
        }
        plugins.clear();
        functionRegistry.clear();
        while (refs.size() > 0) {
            WeakReference<JSValue> it = refs.removeFirst();
            JSValue tmp = it.get();
            if (tmp != null) {
                tmp.close();
            }
        }
        super.close();
        checkReleaseObjPtrPool();
        QuickJS._releaseContext(contextPtr);
        QuickJS.sContextMap.remove(getContextPtr());
    }

    protected Object executeScript(TYPE expectedType, String source, String fileName) throws QuickJSScriptException {
        Object object = QuickJS._executeScript(this.getContextPtr(), expectedType.value, source, fileName, QuickJS.JS_EVAL_TYPE_GLOBAL);
        QuickJS.checkException(context);
        return object;
    }

    /**
     * @return Integer/Double/Boolean/String/JSObject/JSArray/JSFunction
     */
    public Object executeScript(String source, String fileName) throws QuickJSScriptException {
        return executeScript(JSValue.TYPE.UNKNOWN, source, fileName);
    }

    public Object executeScript(String source, String fileName, int evalType) throws QuickJSScriptException {
        Object object = QuickJS._executeScript(this.getContextPtr(), JSValue.TYPE.UNKNOWN.value, source, fileName, evalType);
        QuickJS.checkException(context);
        return object;
    }

    public Object executeModuleScript(String source, String fileName, int evalType) throws QuickJSScriptException {
        Object object = QuickJS._executeScript(this.getContextPtr(), JSValue.TYPE.UNKNOWN.value, source, fileName, QuickJS.JS_EVAL_TYPE_MODULE);
        QuickJS.checkException(context);
        return object;
    }


    public int executeIntegerScript(String source, String fileName) throws QuickJSScriptException {
        return (int) executeScript(JSValue.TYPE.INTEGER, source, fileName);
    }

    public double executeDoubleScript(String source, String fileName) throws QuickJSScriptException {
        return (double) executeScript(JSValue.TYPE.DOUBLE, source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) throws QuickJSScriptException {
        return (boolean) executeScript(JSValue.TYPE.BOOLEAN, source, fileName);
    }

    public String executeStringScript(String source, String fileName) throws QuickJSScriptException {
        return (String) executeScript(JSValue.TYPE.STRING, source, fileName);
    }

    public void executeVoidScript(String source, String fileName) throws QuickJSScriptException {
        executeScript(JSValue.TYPE.NULL, source, fileName);
    }

    public JSArray executeArrayScript(String source, String fileName) throws QuickJSScriptException {
        return (JSArray) executeScript(JSValue.TYPE.JS_ARRAY, source, fileName);
    }

    public JSObject executeObjectScript(String source, String fileName) throws QuickJSScriptException {
        return (JSObject) executeScript(JSValue.TYPE.JS_OBJECT, source, fileName);
    }

    public boolean isReleased() {
        return this.released;
    }

    void registerCallback(JavaCallback callback, JSFunction functionHandle) {
        QuickJS.MethodDescriptor methodDescriptor = new QuickJS.MethodDescriptor();
        methodDescriptor.callback = callback;
        functionRegistry.put(callback.hashCode(), methodDescriptor);
    }

    void registerCallback(JavaVoidCallback callback, JSFunction functionHandle) {
        QuickJS.MethodDescriptor methodDescriptor = new QuickJS.MethodDescriptor();
        methodDescriptor.voidCallback = callback;
        functionRegistry.put(callback.hashCode(), methodDescriptor);
    }

    void checkRuntime(JSValue value) {
        if (value != null && !value.isUndefined()) {
            if (value.context == null) {
                throw new Error("Invalid target runtime");
            }
            QuickJS quickJS = value.context.quickJS;
            if (quickJS == null || quickJS.isReleased() || quickJS != this.quickJS) {
                throw new Error("Invalid target runtime");
            }
        }
    }

    public void addPlugin(Plugin plugin) {
        checkReleased();
        if (plugins.contains(plugin)) {
            return;
        }
        plugin.setup(context);
        this.plugins.add(plugin);
    }

    void checkReleased() {
        checkReleaseObjPtrPool();
        this.quickJS.checkReleased();
        if (this.isReleased()) {
            throw new Error("Context disposed error");
        }
    }
}
