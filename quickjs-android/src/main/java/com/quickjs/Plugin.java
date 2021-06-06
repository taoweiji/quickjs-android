package com.quickjs;

public abstract class Plugin {
    protected abstract void setup(JSContext context);

    protected abstract void close(JSContext context);
}
