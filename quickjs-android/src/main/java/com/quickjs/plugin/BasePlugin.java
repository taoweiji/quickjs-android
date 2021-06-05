package com.quickjs.plugin;

import com.quickjs.JSContext;

public abstract class BasePlugin {
    public abstract void setup(JSContext context);

    public abstract void close(JSContext context);
}
