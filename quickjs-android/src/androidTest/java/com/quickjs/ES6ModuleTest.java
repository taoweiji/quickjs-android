package com.quickjs;

import com.quickjs.plugin.ConsolePlugin;

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
    private String log;

    @Before
    public void setUp() throws Throwable {
        quickJS = QuickJS.createRuntime();
        es6Module = new ES6Module(quickJS) {
            @Override
            protected String getModuleScript(String moduleName) {
                if (moduleName.contains("a.js")) {
                    return "export var name = 'Hello world'; ";
                }
                if (moduleName.contains("b.js")) {
                    return "import {name} from './a.js';console.log(name);";
                }
                return null;
            }
        };
        es6Module.getContext().addPlugin(new ConsolePlugin() {
            @Override
            public void println(int priority, String msg) {
                super.println(priority, msg);
                log = msg;
            }
        });
    }

    @After
    public void tearDown() throws Throwable {
        quickJS.close();
    }

    @Test
    public void executeScript() {
        es6Module.executeScript("import {name} from './a.js';\n console.log(name);", null);
        assertEquals("Hello world", log);
    }

    @Test
    public void execute() {
        es6Module.execute("b.js");
        assertEquals("Hello world", log);
    }
}