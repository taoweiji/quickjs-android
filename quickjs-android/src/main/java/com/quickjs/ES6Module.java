package com.quickjs;

/**
 * 支持 import、export
 */
public abstract class ES6Module extends Module {

    public ES6Module(QuickJS quickJS) {
        super(quickJS, QuickJS._createContext(quickJS.runtimePtr));
    }

    @Override
    protected abstract String getModuleScript(String moduleName);

    public void executeModuleScript(String source, String moduleName) {
        QuickJS._executeScript(context.getContextPtr(), JSValue.TYPE.NULL.value, source, moduleName, QuickJS.JS_EVAL_TYPE_MODULE);
    }

    public Object executeGlobalScript(String source, String fileName) {
        return super.executeScript(JSValue.TYPE.UNKNOWN, source, fileName);
    }

    public void executeModule(String moduleName) {
        String script = getModuleScript(moduleName);
        if (script == null) {
            throw new RuntimeException("'moduleName' script is null");
        }
        executeModuleScript(script, moduleName);
    }
}
