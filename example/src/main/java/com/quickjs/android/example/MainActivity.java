package com.quickjs.android.example;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.quickjs.android.JSContext;
import com.quickjs.android.JSObject;
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

//        jsContext.add("a", 1.1);
//        jsContext.add("b", 2.2);
//        jsContext.add("d", false);
        String result = jsContext.executeStringScript("console.a;", "file.js");
//        String result = jsContext.executeStringScript("var a = 'Hello World';\nb;", "file.js");
        Log.e("quickjs", String.valueOf(result));
//        jsContext.close();
//        quickJS.close();
    }

    void testV8() {
        V8 v8 = V8.createV8Runtime();
        V8Object jsObject = new V8Object(v8);
//        jsObject.executeBooleanFunction();
//        v8.executeVoidFunction();
//        v8.add()
//        v8.add()
//        jsObject.add()
        v8.close();

    }

}