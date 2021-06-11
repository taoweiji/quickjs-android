package com.quickjs;

import com.quickjs.plugin.ConsolePlugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CommonJSModuleTest extends BaseTest{
    private QuickJS quickJS;
    private CommonJSModule module;
    private final List<String> logs = new ArrayList<>();

    @Before
    public void setUp() {
        quickJS = createQuickJS();
        module = new CommonJSModule(quickJS) {
            @Override
            protected String getModuleScript(String moduleName) {
                if (moduleName.contains("a.js")) {
                    return "module.exports.name = 'Hello world';\n" +
                            "module.exports.age = 18;";
                }
                if (moduleName.contains("b.js")) {
                    return "var a = require('./page/a.js');\n" +
                            "console.log(a.name);\n" +
                            "console.log(a.age);";
                }
                return null;
            }
        };
        module.addPlugin(new ConsolePlugin() {
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
    public void executeGlobalScript() {

    }

    @Test
    public void executeModuleScript() {
        module.executeModuleScript("var a = require('./a.js');\n" +
                "console.log(a.name);\n" +
                "console.log(a.age);", null);
        assertEquals("Hello world", logs.get(0));
    }


    @Test
    public void executeModule() {
        module.executeModule("b.js");
        module.executeModule("b.js");
        assertEquals("Hello world", logs.get(0));
        assertEquals("18", logs.get(1));
    }
}