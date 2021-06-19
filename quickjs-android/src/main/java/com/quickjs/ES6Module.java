package com.quickjs;

/**
 * 支持 import、export
 */
public abstract class ES6Module extends Module {

    public ES6Module(QuickJS quickJS) {
        super(quickJS, quickJS.getNative()._createContext(quickJS.runtimePtr));
    }

    @Override
    protected abstract String getModuleScript(String moduleName);

    public void executeModuleScript(String source, String moduleName) {
        checkReleased();
        getNative()._executeScript(context.getContextPtr(), JSValue.TYPE.NULL.value, source, moduleName, QuickJS.JS_EVAL_TYPE_MODULE);
    }

    public void executeModule(String moduleName) {
        String script = getModuleScript(moduleName);
        if (script == null) {
            throw new RuntimeException("'moduleName' script is null");
        }
        executeModuleScript(script, moduleName);
    }
}
