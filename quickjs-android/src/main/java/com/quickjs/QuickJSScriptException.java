package com.quickjs;

public class QuickJSScriptException extends QuickJSException{

    public QuickJSScriptException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuickJSScriptException(Throwable cause) {
        super(cause);
    }

}
