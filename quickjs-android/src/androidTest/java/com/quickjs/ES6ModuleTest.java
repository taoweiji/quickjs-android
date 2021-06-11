package com.quickjs;

import com.quickjs.plugin.ConsolePlugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ES6ModuleTest  extends BaseTest{
    private QuickJS quickJS;
    private ES6Module es6Module;
    private final List<String> logs = new ArrayList<>();

    @Before
    public void setUp() {
        quickJS = createQuickJS();
        es6Module = new ES6Module(quickJS) {
            @Override
            protected String getModuleScript(String moduleName) {
                if (moduleName.contains("a.js")) {
                    return "export var name = 'Hello world';\n" +
                            "export var age = 18;";
                }
                if (moduleName.contains("b.js")) {
                    return "import {name, age} from './page/a.js';\n" +
                            "console.log(name);\n" +
                            "console.log(age);";
                }
                return null;
            }
        };
        es6Module.addPlugin(new ConsolePlugin() {
            @Override
            public void println(int priority, String msg) {
                super.println(priority, msg);
                logs.add(msg);
            }
        });
    }

    @After
    public void tearDown() {
        quickJS.close();
    }

    @Test
    public void executeModuleScript1() {
        es6Module.executeModuleScript("import {name} from './a.js';\n console.log(name);", null);
        assertEquals("Hello world", logs.get(0));
    }

    @Test
    public void executeModuleScript2() {
        es6Module.executeModuleScript("import {name} from './a.js';\n console.log(name);", null);
        assertEquals("Hello world", logs.get(0));
    }


    @Test
    public void executeModule() {
        es6Module.executeModule("b.js");
        es6Module.executeModule("b.js");
        assertEquals("Hello world", logs.get(0));
        assertEquals("18", logs.get(1));
    }
}