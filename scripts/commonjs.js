var __commonjs_modules = {};
function require(path) {
    var parentModule = this['module']
    if (parentModule == undefined) {
        parentModule = { exports: {}, children: [] };
    }
    var tmp = __commonjs_modules[path];
    if (tmp != undefined) {
        return tmp.exports;
    }
    return (function () {
        var module = { exports: {}, children: [] };
        module.id = path;
        module.filename = path;
        module.parent = parentModule;
        eval(commonJSPlugin.require(parentModule, path));
        parentModule.children.push(module);
        __commonjs_modules[path] = module;
        return module.exports;
    })();
}