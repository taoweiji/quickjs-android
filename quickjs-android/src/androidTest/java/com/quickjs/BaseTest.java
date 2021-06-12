package com.quickjs;

public class BaseTest {
    public QuickJS createQuickJS() {
        return QuickJS.createRuntimeWithEventQueue();
    }
}
