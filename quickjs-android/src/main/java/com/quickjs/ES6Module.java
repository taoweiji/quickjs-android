package com.quickjs;

public abstract class ES6Module {
    JSContext context;

    public ES6Module(QuickJS quickJS) {
        context = quickJS.createContext();
        context.modulePlugin = this;
    }

    protected abstract String getModuleScript(String moduleName);

    public void executeScript(String source, String moduleName) {
        QuickJS._executeScript(context.getContextPtr(), JSValue.TYPE.NULL.value, source, moduleName, QuickJS.JS_EVAL_TYPE_MODULE);
    }

    public JSContext getContext() {
        return context;
    }
}
