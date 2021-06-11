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
        jsContext = quickJS.createContext();
        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 10; i++) {
                    new JSObject(jsContext);
                    new JSArray(jsContext);
                }
            }
        });
    }


    void testV8() {
//        V8 v8 = V8.createV8Runtime();
//        v8.executeVoidScript("a.a");
//        WebView webView = new WebView(this);
//        @JavascriptInterface
//        webView.addJavascriptInterface();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quickJS.close();
    }
}