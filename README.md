# quickjs-android
quickjs-android 是 QuickJS JavaScript 引擎的 JSI 框架，实现了 Java 调用内核功能。整体大小仅 500KB，是 Google V8 的不错替代品，启动速度比 V8更快，完整支持[ES2020](https://tc39.github.io/ecma262/)。

> 框架目前还处于开发阶段，切莫在正式项目中使用。

### 使用教程

##### 引入依赖

```groovy
implementation 'com.taoweiji.quickjs:quickjs-android:0.1.0'
```

##### 简单示例

```java
QuickJS quickJS = QuickJS.createV8Runtime();
JSContext jsContext = quickJS.createContext();
int result = jsContext.executeIntegerScript("var a = 2+10;\n a;", "file.js");
jsContext.close();
quickJS.close();
```

