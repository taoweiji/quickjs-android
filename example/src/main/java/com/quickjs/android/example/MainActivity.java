package com.quickjs.android.example;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.v8.V8;
import com.quickjs.android.JSContext;
import com.quickjs.android.QuickJS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuickJS quickJS = QuickJS.createV8Runtime();
        JSContext jsContext = quickJS.createContext();
        int resultInt = jsContext.executeIntegerScript("var a = 2+10;\n a;", "file.js");
        double resultDouble = jsContext.executeDoubleScript("var a = 2.0;\n a;", "file.js");
        boolean resultBool = jsContext.executeBooleanScript("var a = 1 > 0;\n a;", "file.js");
        String resultString = jsContext.executeStringScript("var a = 'Hello World';\n a;", "file.js");
        Log.e("quickjs", String.valueOf(resultInt));
        Log.e("quickjs", String.valueOf(resultDouble));
        Log.e("quickjs", String.valueOf(resultBool));
        Log.e("quickjs", String.valueOf(resultString));
        jsContext.close();
        quickJS.close();
    }

    void test() {
        V8 v8 = V8.createV8Runtime();
        QuickJS quickJS = QuickJS.createV8Runtime();
        JSContext jsContext = quickJS.createContext();
        v8.close();

    }

    static {
        System.loadLibrary("quickjs");
        System.loadLibrary("quickjs-android");
    }
}