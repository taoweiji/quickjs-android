package com.quickjs;

public class QuickJSException extends RuntimeException {
    private String name;

    public QuickJSException(String name, String message) {
        super(name + "," + message);
        this.name = name;
    }

    public QuickJSException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuickJSException(Throwable cause) {
        super(cause);
    }

    public String getName() {
        return name;
    }
}
