package com.quickjs.android;

public class QuickJS {
    public static native String execute(String globalAlias, String tempDirectory);
}
