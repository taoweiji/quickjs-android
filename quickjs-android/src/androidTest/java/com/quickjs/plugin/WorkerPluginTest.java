package com.quickjs.plugin;

import com.quickjs.CommonJSModule;
import com.quickjs.JSContext;
import com.quickjs.QuickJS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkerPluginTest {

    WorkerPlugin plugin;

    @Before
    public void setUp() throws Exception {
        plugin = new WorkerPlugin(null) {
            @Override
            protected void setup(JSContext context) {
                super.setup(context);
            }

            @Override
            protected JSContext createContext(QuickJS quickJS) {
                return new CommonJSModule(quickJS) {
                    @Override
                    protected String getModuleScript(String moduleName) {
                        return null;
                    }
                };
            }
        };
        plugin.start();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void postMessage() {
    }

    @Test
    public void setup() {
    }
}