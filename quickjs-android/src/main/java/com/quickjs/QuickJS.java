package com.quickjs;

import androidx.annotation.Keep;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public class QuickJS implements Closeable {
    private final long runtimePtr;
    static final Map<Long, JSContext> sContextMap = new HashMap<>();
    private boolean released;

    private QuickJS(long runtimePtr) {
        this.runtimePtr = runtimePtr;
    }

    public static QuickJS createRuntime() {
        return new QuickJS(_createRuntime());
    }

    public JSContext createContext() {
        JSContext context = new JSContext(this, _createContext(runtimePtr));
        sContextMap.put(context.getContextPtr(), context);
        return context;
    }

    public void close() {
        if (this.released) {
            return;
        }
        this.released = true;
        JSContext[] values = new JSContext[sContextMap.size()];
        sContextMap.values().toArray(values);
        for (JSContext context : values) {
            if (context.quickJS == this) {
                context.close();
            }
        }
        _releaseRuntime(runtimePtr);
    }

    static class MethodDescriptor {
        public JavaVoidCallback voidCallback;
        public JavaCallback callback;
    }

    @Keep
    static Object callJavaCallback(long context_ptr, int javaCallerId, JSValue objectHandle, JSArray argsHandle, boolean void_method) {
        JSContext context = sContextMap.get(context_ptr);
        if (context == null) {
            return null;
        }
        MethodDescriptor methodDescriptor = context.functionRegistry.get(javaCallerId);
        if (methodDescriptor == null) return null;
        JSObject receiver = null;
        if (objectHandle instanceof JSObject) {
            receiver = (JSObject) objectHandle;
        }
        if (void_method) {
            methodDescriptor.voidCallback.invoke(receiver, argsHandle);
            return null;
        }
        return methodDescriptor.callback.invoke(receiver, argsHandle);
    }

    @Keep
    static JSValue createJSValue(long contextPtr, int type, long tag, int u_int32, double u_float64, long u_ptr) {
        JSContext context = sContextMap.get(contextPtr);
        switch (type) {
            case JSValue.TYPE_JS_FUNCTION:
                return new JSFunction(context, tag, u_int32, u_float64, u_ptr);
            case JSValue.TYPE_JS_ARRAY:
                return new JSArray(context, tag, u_int32, u_float64, u_ptr);
            case JSValue.TYPE_JS_OBJECT:
                return new JSObject(context, tag, u_int32, u_float64, u_ptr);
            case JSValue.TYPE_UNDEFINED:
                return new JSObject.Undefined(context, tag, u_int32, u_float64, u_ptr);
            default:
                return new JSValue(context, tag, u_int32, u_float64, u_ptr);
        }
    }

    @Keep
    static String getModuleScript(long contextPtr, String moduleName) {
        JSContext context = sContextMap.get(contextPtr);
        if (context == null){
            return null;
        }
        return context.getModuleScript(moduleName);
    }

    static Object executeFunction(JSContext context, JSValue objectHandle, String name, Object[] parameters) {
        JSArray args = new JSArray(context);
        if (parameters != null) {
            for (Object item : parameters) {
                if (item instanceof Integer) {
                    args.push((int) item);
                } else if (item instanceof Double) {
                    args.push((double) item);
                } else if (item instanceof Boolean) {
                    args.push((boolean) item);
                } else if (item instanceof String) {
                    args.push((String) item);
                } else if (item instanceof JSValue) {
                    args.push((JSValue) item);
                } else {
                    args.push((JSValue) null);
                }
            }
        }
        return _executeFunction(context.getContextPtr(), JSValue.TYPE_UNKNOWN, objectHandle, name, args);
    }

    static void checkException(JSContext context) {
        String[] result = QuickJS._getException(context.getContextPtr());
        if (result == null) {
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append(result[1]).append('\n');
        for (int i = 2; i < result.length; i++) {
            message.append(result[i]);
        }
        throw new QuickJSException(result[0], message.toString());
    }


    public boolean isReleased() {
        return this.released;
    }

    static native long _createRuntime();

    static native void _releaseRuntime(long runtimePtr);

    static native long _createContext(long runtimePtr);

    static native void _releaseContext(long contextPtr);

    static native Object _executeScript(long contextPtr, int expectedType, String source, String fileName, int eval_flags);

    static native JSObject _getGlobalObject(long contextPtr);

    static native void _set(long contextPtr, JSValue objectHandle, String key, Object value);

    static native Object _get(long contextPtr, int expectedType, JSValue objectHandle, String key);

    static native Object _arrayGet(long contextPtr, int expectedType, JSValue objectHandle, int index);

    static native void _arrayAdd(long contextPtr, JSValue objectHandle, Object value);

    static native Object _executeFunction(long contextPtr, int expectedType, JSValue objectHandle, String name, JSValue parametersHandle);

    static native Object _executeFunction2(long contextPtr, int expectedType, JSValue objectHandle, JSValue functionHandle, JSValue parametersHandle);

    static native JSObject _initNewJSObject(long contextPtr);

    static native JSArray _initNewJSArray(long contextPtr);

    native static JSFunction _initNewJSFunction(long contextPtr, int javaCallerId, boolean voidMethod);

    static native void _release(long contextPtr, JSValue objectHandle);

    static native JSFunction _registerJavaMethod(long contextPtr, JSValue objectHandle, String jsFunctionName, int javaCallerId, boolean voidMethod);

    native static int _getObjectType(long contextPtr, JSValue objectHandle);

    native static boolean _contains(long contextPtr, JSValue objectHandle, String key);

    native static String[] _getKeys(long contextPtr, JSValue objectHandle);

    native static boolean _isUndefined(long contextPtr, JSValue value);

    native static JSValue _Undefined(long contextPtr);

    native static JSValue _getValue(long contextPtr, JSObject object, String key);

    native static JSValue _arrayGetValue(long contextPtr, JSArray array, int index);

    native static String[] _getException(long contextPtr);

    /* JS_Eval() flags */
    static int JS_EVAL_TYPE_GLOBAL = (0); /* global code (default) */
    static int JS_EVAL_TYPE_MODULE = (1); /* module code */
    static int JS_EVAL_TYPE_MASK = (3);
    static int JS_EVAL_FLAG_STRICT = (1 << 3); /* force 'strict' mode */
    static int JS_EVAL_FLAG_STRIP = (1 << 4); /* force 'strip' mode */
    /* compile but do not run. The result is an object with a
       JS_TAG_FUNCTION_BYTECODE or JS_TAG_MODULE tag. It can be executed
       with JS_EvalFunction(). */
    static int JS_EVAL_FLAG_COMPILE_ONLY = (1 << 5);
    /* don't include the stack frames before this eval in the Error() backtraces */
    static int JS_EVAL_FLAG_BACKTRACE_BARRIER = (1 << 6);


    static {
        System.loadLibrary("quickjs");
        System.loadLibrary("quickjs-android");
    }
}
