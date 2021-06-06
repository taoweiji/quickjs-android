package com.quickjs;

public abstract class ES6Module extends JSContext {

    ES6Module(QuickJS quickJS) {
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

    @Deprecated
    @Override
    public Object executeScript(String source, String fileName) throws QuickJSScriptException {
        throw new UnsupportedOperationException("Please use the executeModuleScript/executeGlobalScript/executeModule");
    }

    @Deprecated
    @Override
    public int executeIntegerScript(String source, String fileName) throws QuickJSScriptException {
        return super.executeIntegerScript(source, fileName);
    }

    @Deprecated
    @Override
    public double executeDoubleScript(String source, String fileName) throws QuickJSScriptException {
        return super.executeDoubleScript(source, fileName);
    }

    @Deprecated
    @Override
    public boolean executeBooleanScript(String source, String fileName) throws QuickJSScriptException {
        return super.executeBooleanScript(source, fileName);
    }

    @Deprecated
    @Override
    public String executeStringScript(String source, String fileName) throws QuickJSScriptException {
        return super.executeStringScript(source, fileName);
    }

    @Deprecated
    @Override
    public void executeVoidScript(String source, String fileName) throws QuickJSScriptException {
        super.executeVoidScript(source, fileName);
    }

    @Deprecated
    @Override
    public JSArray executeArrayScript(String source, String fileName) throws QuickJSScriptException {
        return super.executeArrayScript(source, fileName);
    }

    @Deprecated
    @Override
    public JSObject executeObjectScript(String source, String fileName) throws QuickJSScriptException {
        return super.executeObjectScript(source, fileName);
    }
}
