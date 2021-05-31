package com.quickjs.android.example;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSObject;
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
    }

    void test() {
        jsContext.executeVoidScript("function test(data){ return data}", "file.js");
        new JSArray(jsContext);
        new JSObject(jsContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        jsContext.close();
        quickJS.close();
    }
}