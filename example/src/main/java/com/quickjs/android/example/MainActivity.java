package com.quickjs.android.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.v8.V8;
import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSFunction;
import com.quickjs.JSObject;
import com.quickjs.JSValue;
import com.quickjs.JavaCallback;
import com.quickjs.JavaVoidCallback;
import com.quickjs.QuickJS;

public class MainActivity extends AppCompatActivity {

    private QuickJS quickJS;
    private JSContext jsContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quickJS = QuickJS.createRuntime();
//        jsContext = quickJS.createContext();
//        jsContext = quickJS.createContext();
//        test();
//        testV8();
        QuickJS quickJS = QuickJS.createRuntime();
        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 10000; i++) {
//                    quickJS.createContext();
                }
            }
        });
        test();
        addJavascriptInterface();
    }

    void test() {
        JSContext context = quickJS.createContext();

        JSObject user = new JSObject(context).set("name", "Wiki").set("age", 18);
        user.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(JSObject receiver, JSArray args) {
                Log.e("QuickJS", args.getString(0));
            }
        }, "log");
        user.executeVoidFunction("log", new JSArray(context).push("Hello World"));
    }

    void testV8() {
//        V8 v8 = V8.createV8Runtime();
//        v8.executeVoidScript("a.a");
        WebView webView = new WebView(this);
//        @JavascriptInterface
//        webView.addJavascriptInterface();
    }

    public void addJavascriptInterface() {
        JSContext context = quickJS.createContext();
        context.addJavascriptInterface(new Console(), "console");
        context.executeVoidScript("console.log('Hello World')", null);
        context.executeVoidScript("console.error('Hello World')", null);
        context.executeVoidScript("console.info('Hello World')", null);
        int count = context.executeIntegerScript("console.count()", null);
        Log.d("console", String.valueOf(count));
    }

    public static class Console {
        int count = 0;

        @JavascriptInterface
        public void log(String msg) {
            count++;
            Log.d("console", msg);
        }

        @JavascriptInterface
        public void info(String msg) {
            count++;
            Log.i("console", msg);
        }

        @JavascriptInterface
        public void error(String msg) {
            count++;
            Log.e("console", msg);
        }

        @JavascriptInterface
        public int count() {
            return count;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        jsContext.close();
//        quickJS.close();
    }
}