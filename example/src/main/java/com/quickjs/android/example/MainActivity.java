package com.quickjs.android.example;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.v8.V8;
import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSObject;
import com.quickjs.JSValue;
import com.quickjs.QuickJS;

public class MainActivity extends AppCompatActivity {

    private QuickJS quickJS;
    private JSContext jsContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quickJS = QuickJS.createRuntime();
        jsContext = quickJS.createContext();
        jsContext = quickJS.createContext();
        test();
        testV8();
    }

    void test() {
        JSValue result = jsContext.executeObjectScript2("a.a", "file.js");
//        JSValue.TYPE type = result.getType();
        Log.e("QuickJS", result.getClass().getName());
    }

    void testV8() {
//        V8 v8 = V8.createV8Runtime();
//        v8.executeVoidScript("a.a");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        jsContext.close();
        quickJS.close();
    }
}