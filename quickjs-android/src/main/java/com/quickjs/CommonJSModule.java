package com.quickjs;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持 require、exports
 */
public abstract class CommonJSModule extends Module {
    private final String MODULE_SCRIPT_WRAPPER = "(function () {var module = { exports: {}, children: [] }; #CODE ; return module;})();";
    private final Map<String, JSObject> modules = new HashMap<>();

    CommonJSModule(QuickJS quickJS) {
        super(quickJS, QuickJS._createContext(quickJS.runtimePtr));
        registerJavaMethod((receiver, args) -> {
            String path = args.getString(0);
            JSObject module = modules.get(path);
            if (module == null) {
                // TODO
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
}
