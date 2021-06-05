package com.quickjs.plugin;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSObject;
import com.quickjs.JSValue;
import com.quickjs.JavaCallback;

/**
 * 实现 CommonJS 的能力
 * http://javascript.ruanyifeng.com/nodejs/module.html
 */
public class CommonJSPlugin extends Plugin {
    private final String commonJSScript = "var __commonjs_modules = {};\n" +
            "function require(path) {\n" +
            "    var parentModule = this['module']\n" +
            "    if (parentModule == undefined) {\n" +
            "        parentModule = { exports: {}, children: [] };\n" +
            "    }\n" +
            "    var tmp = __commonjs_modules[path];\n" +
            "    if (tmp != undefined) {\n" +
            "        return tmp.exports;\n" +
            "    }\n" +
            "    return (function () {\n" +
            "        var module = { exports: {}, children: [] };\n" +
            "        module.id = path;\n" +
            "        module.filename = path;\n" +
            "        module.parent = parentModule;\n" +
            "        eval(commonJSPlugin.require(parentModule, path));\n" +
            "        parentModule.children.push(module);\n" +
            "        __commonjs_modules[path] = module;\n" +
            "        return module.exports;\n" +
            "    })();\n" +
            "}";

    public String require(Module parent, String path) {
        return "";
    }

    @Override
    public void setup(JSContext context) {
        JSObject commonJSPlugin = new JSObject(context);
        commonJSPlugin.registerJavaMethod((receiver, args) -> {
            JSObject parentObject = args.getObject(0);
            Module parent = parseModule(parentObject);
            String path = args.getString(1);
            return CommonJSPlugin.this.require(parent, path);
        }, "require");
        context.set("commonJSPlugin", commonJSPlugin);
        context.executeVoidScript(commonJSScript, "commonJSPlugin.js");
    }

    private Module parseModule(JSObject parent) {
        if (parent == null || parent.isUndefined()) {
            return null;
        }
        Module module = new Module();
        module.id = parent.getString("id");
        module.filename = module.id;
        module.parent = parseModule(parent.getObject("parent"));
        module.exports = parent.get(JSValue.TYPE.UNKNOWN, "exports");
        return module;
    }

    @Override
    public void close(JSContext context) {

    }

    public static class Module {
        public String id;
        public String filename;
        public Module parent;
        public JSObject parentObj;
        public Object exports;
    }
}