package com.quickjs.android.example;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.quickjs.android.JSArray;
import com.quickjs.android.JSContext;
import com.quickjs.android.JSObject;
import com.quickjs.android.JavaVoidCallback;
import com.quickjs.android.QuickJS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        QuickJS quickJS = QuickJS.createV8Runtime();
//        JSContext jsContext = quickJS.createContext();
//        int resultInt = jsContext.executeIntegerScript("var a = 2+10;\n a;", "file.js");
//        double resultDouble = jsContext.executeDoubleScript("var a = 2.0;\n a;", "file.js");
//        boolean resultBool = jsContext.executeBooleanScript("var a = 1 > 0;\n a;", "file.js");
//        String resultString = jsContext.executeStringScript("var a = 'Hello World';\n a;", "file.js");
//        Log.e("quickjs", String.valueOf(resultInt));
//        Log.e("quickjs", String.valueOf(resultDouble));
//        Log.e("quickjs", String.valueOf(resultBool));
//        Log.e("quickjs", String.valueOf(resultString));
//        jsContext.add("", 1);
//        jsContext.close();
//        quickJS.close();
        test();
    }

    void test() {
        QuickJS quickJS = QuickJS.createV8Runtime();
        JSContext jsContext = quickJS.createContext();
//        jsContext.add("b", "Hello World");
        JSObject console = new JSObject(jsContext);
        jsContext.add("console", console);
        console.add("a", "Hello");
        console.add("b", 3.14159);
        console.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject jsObject, JSArray jsArray) {
                Log.e("console", jsArray.getString(0));
            }
        }, "log");
        jsContext.executeScript("function name() {return 'Hello World'};", "file.js");
//        String result = jsContext.executeStringFunction("name", null);
        String result = jsContext.executeStringScript("name()", "file.js");
        Log.e("QuickJS", String.valueOf(result));
//        jsContext.close();
//        quickJS.close();
    }

    void testV8() {
        V8 v8 = V8.createV8Runtime();
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