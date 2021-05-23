package com.quickjs.android.example;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.quickjs.android.QuickJS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int result = QuickJS.executeIntegerScript("var a = 2+2;\n a;","file.js");
        Log.e("quickjs", String.valueOf(result));
    }

    static {
        System.loadLibrary("quickjs");
        System.loadLibrary("quickjs-android");
    }
}