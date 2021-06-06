package com.quickjs;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持 require、exports
 */
public abstract class CommonJSModule extends JSContext {
    private final String MODULE_SCRIPT_WRAPPER = "(function () {var module = { exports: {}, children: [] }; #CODE ; return module;})();";
    private final Map<String, JSObject> modules = new HashMap<>();

    CommonJSModule(QuickJS quickJS) {
        super(quickJS, QuickJS._createContext(quickJS.runtimePtr));
        registerJavaMethod((receiver, args) -> {
            String path = args.getString(0);
            JSObject module = modules.get(path);
            if (module == null) {
                module = executeModule(path);
            }
            return module.get(TYPE.UNKNOWN, "exports");
        }, "require");
    }

    @Override
    public void close() {
        modules.clear();
        super.close();
    }

    @Override
    protected abstract String getModuleScript(String moduleName);

    public JSObject executeModuleScript(String source, String moduleName) {
        String wrapper = MODULE_SCRIPT_WRAPPER.replace("#CODE", source);
        JSObject module = (JSObject) super.executeScript(TYPE.UNKNOWN, wrapper, moduleName);
        modules.put(moduleName, module);
        return module;
    }

    public Object executeGlobalScript(String source, String fileName) {
        return super.executeScript(TYPE.UNKNOWN, source, fileName);
    }

    public JSObject executeModule(String moduleName) {
        String script = getModuleScript(moduleName);
        if (script == null) {
            throw new RuntimeException("'moduleName' script is null");
        }
        return executeModuleScript(script, moduleName);
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
