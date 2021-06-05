package com.quickjs.plugin;

import com.quickjs.JSContext;

public abstract class Plugin {
    public abstract void setup(JSContext context);

    public abstract void close(JSContext context);
}
