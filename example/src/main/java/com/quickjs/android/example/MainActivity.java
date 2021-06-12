package com.quickjs.android.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSFunction;
import com.quickjs.JSObject;
import com.quickjs.JavaVoidCallback;
import com.quickjs.Plugin;
import com.quickjs.QuickJS;
import com.quickjs.plugin.ConsolePlugin;

public class MainActivity extends AppCompatActivity {

    private QuickJS quickJS;
    private JSContext jsContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quickJS = QuickJS.createRuntimeWithEventQueue();
        jsContext = quickJS.createContext();
        jsContext.addPlugin(new ConsolePlugin() {
            @Override
            public void println(int priority, String msg) {
                Log.e("MainActivity", Thread.currentThread().getName());
                super.println(priority, msg);
            }
        });
        jsContext.addPlugin(new Plugin() {
            @Override
            protected void setup(JSContext context) {
                context.registerJavaMethod(new JavaVoidCallback() {
                    @Override
                    public void invoke(JSObject receiver, JSArray args) {
                        JSFunction func = (JSFunction) args.getObject(0);
                        long timer = args.getInteger(1);
                        new Thread(() -> {
                            try {
                                Thread.sleep(timer);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!func.getContext().isReleased()) {
                                func.call(null, null);
                            }
                        }).start();
                    }
                }, "setTimeout");
            }

            @Override
            protected void close(JSContext context) {

            }
        });
        int count = jsContext.executeIntegerScript("var count = 0;count;", null);
        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        jsContext.executeVoidScript("setTimeout(function(){console.log(count++)},1000)", null);
//                        for (int i = 0; i < 10; i++) {
//                            int count = jsContext.executeIntegerScript("console.log(count++);count;", null);
//                            Log.e("console", count + "");
//                        }
                    }
                }).start();
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