package com.quickjs;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class JSContext extends JSObject implements Closeable {
    final QuickJS quickJS;
    final long contextPtr;
    final Set<Plugin> plugins = Collections.synchronizedSet(new HashSet<>());
    final Map<Integer, JSValue> refs = Collections.synchronizedMap(new WeakHashMap<>());
    final List<Object[]> releaseObjPtrPool = Collections.synchronizedList(new LinkedList<>());
    final Map<Integer, QuickJS.MethodDescriptor> functionRegistry = Collections.synchronizedMap(new HashMap<>());

    JSContext(QuickJS quickJS, long contextPtr) {
        super(null, quickJS.getNative()._getGlobalObject(contextPtr));
        this.quickJS = quickJS;
        this.contextPtr = contextPtr;
        this.context = this;
        QuickJS.sContextMap.put(contextPtr, this);
    }

    long getContextPtr() {
        return this.contextPtr;
    }

    void addObjRef(JSValue reference) {
        if (reference.getClass() != JSContext.class) {
            refs.put(reference.hashCode(), reference);
        }
    }

    void releaseObjRef(JSValue reference, boolean finalize) {
        if (finalize) {
            releaseObjPtrPool.add(new Object[]{reference.tag, reference.u_int32, reference.u_float64, reference.u_ptr});
        } else {
            getNative()._releasePtr(getContextPtr(), reference.tag, reference.u_int32, reference.u_float64, reference.u_ptr);
        }
        removeObjRef(reference);
    }

    private void checkReleaseObjPtrPool() {
        while (!releaseObjPtrPool.isEmpty()) {
            Object[] ptr = releaseObjPtrPool.get(0);
            getNative()._releasePtr(getContextPtr(), (long) ptr[0], (int) ptr[1], (double) ptr[2], (long) ptr[3]);
            releaseObjPtrPool.remove(0);
        }
    }

    void removeObjRef(JSValue reference) {
        refs.remove(reference.hashCode());
    }


    @Override
    public void close() {
        postEventQueue(() -> {
            if (released) {
                return;
            }
            for (Plugin plugin : plugins) {
                plugin.close(JSContext.this);
            }
            plugins.clear();
            functionRegistry.clear();
            JSValue[] jsValues = refs.values().toArray(new JSValue[0]);
            for (JSValue it : jsValues) {
                if (it != null) {
                    it.close();
                }
            }
            checkReleaseObjPtrPool();
            JSContext.super.close();
            getNative()._releaseContext(contextPtr);
            QuickJS.sContextMap.remove(getContextPtr());
        });
    }

    protected Object executeScript(TYPE expectedType, String source, String fileName) throws QuickJSException {
        Object object = getNative()._executeScript(this.getContextPtr(), expectedType.value, source, fileName, QuickJS.JS_EVAL_TYPE_GLOBAL);
        QuickJS.checkException(context);
        return object;
    }

    /**
     * @return Integer/Double/Boolean/String/JSObject/JSArray/JSFunction
     */
    public Object executeScript(String source, String fileName) throws QuickJSException {
        return executeScript(JSValue.TYPE.UNKNOWN, source, fileName);
    }

    public Object executeScript(String source, String fileName, int evalType) throws QuickJSException {
        Object object = getNative()._executeScript(this.getContextPtr(), JSValue.TYPE.UNKNOWN.value, source, fileName, evalType);
        QuickJS.checkException(context);
        return object;
    }

    public Object executeModuleScript(String source, String fileName, int evalType) throws QuickJSException {
        Object object = getNative()._executeScript(this.getContextPtr(), JSValue.TYPE.UNKNOWN.value, source, fileName, QuickJS.JS_EVAL_TYPE_MODULE);
        QuickJS.checkException(context);
        return object;
    }


    public int executeIntegerScript(String source, String fileName) throws QuickJSException {
        return (int) executeScript(JSValue.TYPE.INTEGER, source, fileName);
    }

    public double executeDoubleScript(String source, String fileName) throws QuickJSException {
        return (double) executeScript(JSValue.TYPE.DOUBLE, source, fileName);
    }

    public boolean executeBooleanScript(String source, String fileName) throws QuickJSException {
        return (boolean) executeScript(JSValue.TYPE.BOOLEAN, source, fileName);
    }

    public String executeStringScript(String source, String fileName) throws QuickJSException {
        return (String) executeScript(JSValue.TYPE.STRING, source, fileName);
    }

    public void executeVoidScript(String source, String fileName) throws QuickJSException {
        executeScript(JSValue.TYPE.NULL, source, fileName);
    }

    public JSArray executeArrayScript(String source, String fileName) throws QuickJSException {
        return (JSArray) executeScript(JSValue.TYPE.JS_ARRAY, source, fileName);
    }

    public JSObject executeObjectScript(String source, String fileName) throws QuickJSException {
        return (JSObject) executeScript(JSValue.TYPE.JS_OBJECT, source, fileName);
    }

    public boolean isReleased() {
        if (getQuickJS().isReleased()) {
            return true;
        }
        return this.released;
    }

    void _registerCallback(JavaCallback callback, JSFunction functionHandle) {
        QuickJS.MethodDescriptor methodDescriptor = new QuickJS.MethodDescriptor();
        methodDescriptor.callback = callback;
        functionRegistry.put(callback.hashCode(), methodDescriptor);
    }

    void _registerCallback(JavaVoidCallback callback, JSFunction functionHandle) {
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
        if (this.isReleased()) {
            throw new Error("Context disposed error");
        }
    }

    public QuickJSNative getNative() {
        return quickJS.getNative();
    }

    public QuickJS getQuickJS() {
        return quickJS;
    }
}
