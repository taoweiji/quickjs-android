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
            String moduleBaseName = null;
            if (!receiver.isUndefined()) {
                JSObject parentModule = receiver.getObject("module");
                if (!parentModule.isUndefined()) {
                    if (parentModule.contains("filename")) {
                        moduleBaseName = parentModule.getString("filename");
                    }
                }
            }
            String path = args.getString(0);
            String moduleName = convertModuleName(moduleBaseName, path);
            JSObject module = modules.get(path);
            if (module == null) {
                module = executeModule(moduleName);
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
        String moduleName_ = convertModuleName(null, moduleName);
        String wrapper = MODULE_SCRIPT_WRAPPER.replace("#CODE", source);
        JSObject module = (JSObject) super.executeScript(TYPE.UNKNOWN, wrapper, moduleName_);
        module.set("id", moduleName_);
        module.set("filename", moduleName_);
        if (moduleName_ != null) {
            modules.put(moduleName_, module);
        }
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
