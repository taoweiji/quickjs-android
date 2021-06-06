package com.quickjs;

import com.quickjs.ES6Module;
import com.quickjs.JSArray;
import com.quickjs.JSObject;
import com.quickjs.JavaVoidCallback;
import com.quickjs.QuickJS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ES6ModuleTest {
    private QuickJS quickJS;
    private Map<String, String> fileMap = new HashMap<>();
    private ES6Module es6Module;

    @Before
    public void setUp() throws Throwable {
        quickJS = QuickJS.createRuntime();
        es6Module = new ES6Module(quickJS) {
            @Override
            protected String getModuleScript(String moduleName) {
                if (moduleName.contains("a.js")) {
                    return "export var name = \"Hello world\"; ";
                }
                if (moduleName.contains("b.js")) {
                    return "import {name} from './a.js';name;";
                }
                return null;
            }
        };
    }

    @After
    public void tearDown() throws Throwable {
        quickJS.close();
    }

    @Test
    public void require() {
//        fileMap.put("a.js", "module.exports = 'Hello world';");
        Object[] result = new Object[1];
        es6Module.getContext().registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject receiver, JSArray args) {
                result[0] = args.getString(0);
            }
        }, "log");

        es6Module.executeScript("import {name} from './ddd/a.js';\n log(name);", null);
        assertEquals("Hello world", result[0]);
    }

    @Test
    public void getContext() {

    }
}