package com.quickjs.android.example;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.quickjs.android.JSArray;
import com.quickjs.android.JSContext;
import com.quickjs.android.JSObject;
import com.quickjs.android.QuickJS;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test();
    }

    void test() {
        QuickJS quickJS = QuickJS.createRuntime();
        JSContext context = quickJS.createContext();
        context.executeVoidScript("function test(data){ return data}", "file.js");
        JSArray array = new JSArray(context);
        array.push(Integer.MAX_VALUE);
        int result = context.executeIntegerFunction("test", array);
        array.close();
        context.close();
        quickJS.close();
    }

    void testV8() {
        V8 v8 = V8.createV8Runtime();
//        v8.executeStringFunction()
//        v8.contains();
        V8Object jsObject = new V8Object(v8);
        jsObject.registerJavaMethod(new com.eclipsesource.v8.JavaVoidCallback() {

            @Override
            public void invoke(V8Object v8Object, V8Array v8Array) {
//                v8Array.getInteger(v8Array.getString())
            }
        }, "");
//        jsObject.executeBooleanFunction();
//        v8.executeVoidFunction();
//        v8.add()
//        v8.add()
//        jsObject.add()
//        v8.getInteger()
        v8.close();

    }

}