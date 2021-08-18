# quickjs-android
[![Download](https://maven-badges.herokuapp.com/maven-central/io.github.taoweiji.quickjs/quickjs-android/badge.svg)](https://search.maven.org/search?q=io.github.taoweiji.quickjs)


[quickjs-android](https://github.com/taoweiji/quickjs-android) 是 [QuickJS](https://github.com/bellard/quickjs) JavaScript 引擎的 Android 接口框架，整体基于面向对象设计，提供了自动GC功能，使用简单。armeabi-v7a 的大小仅 350KB，是 [Google V8](https://github.com/v8/v8) 不错的替代品，启动速度比 V8 快，内存占用更低，支持 [ES2020](https://tc39.es/ecma262/)。

- armeabi-v7a 平台下，整体占用apk空间仅 350KB；
- JS对象自动GC，无需手动释放；
- 支持 ES6 Module，可以使用 import、export 函数；
- 支持 Node.js 的 CommonJS 规范，可以使用 require、exports 函数；
- 支持绑定 Java 注解函数；
- 支持通过 Java Function Callback 函数注册JS函数；
- 内置 Event Queue，开发者可以在任意线程执行代码，无需关心JS单线程问题；

### 使用教程

##### 引入依赖

```groovy
implementation 'io.github.taoweiji.quickjs:quickjs-android:1.+'
```

##### 简单示例

```java
QuickJS quickJS = QuickJS.createRuntime();
JSContext context = quickJS.createContext();
int result = context.executeIntegerScript("var a = 2+10;\n a;", "file.js");
context.close();
quickJS.close();
```



### 对象介绍

##### QuickJS

运行环境，可以创建多个运行时环境，不同的环境之间不能共享对象，不使用的时候需要销毁。

```java
QuickJS quickJS = QuickJS.createRuntime();
// 如果需要在多线程执行，必须创建带有线程池的环境
// QuickJS quickJS = QuickJS.createRuntimeWithEventQueue();
```

##### JSContext

由 QuickJS 创建，一个 QuickJS 可以创建多个 JSContext，不使用的时候需要销毁。

```java
JSContext context = quickJS.createContext();
int result = context.executeIntegerScript("var a = 2+10;\n a;", "file.js");
String result = context.executeStringScript("'Hello World';", "file.js");
context.close();
```

##### JSObject

```java
JSObject user = new JSObject(context).set("name", "Wiki").set("age", 18).set("time",System.currentTimeMillis());
Log.e("QuickJS", String.valueOf(user.getString("name")));
Log.e("QuickJS", String.valueOf(user.getInteger("age")));
Log.e("QuickJS", String.valueOf(user.getDouble("time")));

user.registerJavaMethod(new JavaVoidCallback() {
    @Override
    public void invoke(JSObject receiver, JSArray args) {
        Log.e("QuickJS", args.getString(0));
    }
}, "log");
user.executeVoidFunction("log", new JSArray(context).push("Hello World"));
```
##### JSArray

```java
JSArray array = new JSArray(context).push(1).push(3.14).push(true).push("Hello World");
Log.e("QuickJS", String.valueOf(array.getInteger(0)));
Log.e("QuickJS", String.valueOf(array.getDouble(1)));
```

##### JSFunction

```java
JSFunction log = new JSFunction(context, new JavaVoidCallback() {
    @Override
    public void invoke(JSObject receiver, JSArray args) {
        Log.e("QuickJS", args.getString(0));
    }
});
JSFunction message = new JSFunction(context, new JavaCallback() {
    @Override
    public Object invoke(JSObject receiver, JSArray array) {
        return "Hello World";
    }
});
context.set("console", new JSObject(context).set("log", log).set("message", message));
context.executeVoidScript("console.log(console.message())", null);
```

##### addJavascriptInterface

```java
public class Console {
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

context.addJavascriptInterface(new Console(), "console");
context.executeVoidScript("console.log('Hello World')", null);
int count = context.executeIntegerScript("console.count()", null);
Log.d("console", String.valueOf(count));
```

#### QuickJS

| 方法                             | 说明       |
| -------------------------------- | ---------- |
| static QuickJS createRuntime() | 创建运行时 |
| JSContext createContext()        | 创建上下文 |
| void close()                     | 销毁引擎 |

#### JSValue

对象会自动回收，开发者无需手动close()

| 方法                                         | 说明              |
| -------------------------------------------- | ----------------- |
| static JSObject Undefined(JSContext context) | 获取Undefined对象 |
| static JSValue NULL()                        | 获取NULL对象      |
| TYPE getType()                               | 获取数据类型      |
| boolean isUndefined()                        |                   |


#### JSObject

继承JSValue

| 方法                           | 说明              |
| ------------------------------ | ----------------- |
| set(key, value)                | 设置属性，支持int、boolean、double、String、JSValue |
| int getInteger(String key)     | 返回值int对象值，如果没有就会返回0 |
| boolean getBoolean(String key) | 返回值boolean对象值，如果没有就会返回false |
| double getDouble(String key)   | 返回值double对象值，如果没有就会返回0 |
| String getString(String key)   | 返回值String对象值，如果没有就会返回null |
| JSArray getArray(String key)   | 返回值JSArray对象值，如果没有就会返回null |
| JSObject getObject(String key) | 可能会返回JSObject、JSArray、JSFunction，如果没有就会返回null |
| registerJavaMethod(JavaCallback callback, String jsFunctionName) | 注册JS函数，调用函数会执行java的Callback，带有返回值 |
| registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) | 注册JS函数，调用函数会执行java的Callback，不带返回值 |
| Object executeFunction(String name, JSArray parameters) | 可能会返回Integer、Double、Boolean、String、JSArray、JSObject、JSFunction、null |
| double executeDoubleFunction(String name, JSArray parameters) | 返回 double，默认返回 0                     |
| boolean executeBooleanFunction(String name, JSArray parameters) | 返回boolean，默认人会false                |
| String executeStringFunction(String name, JSArray parameters) | 返回String，默认返回null |
| JSArray executeArrayFunction(String name, JSArray parameters) | 返回JSArray，默认返回null |
| JSObject executeObjectFunction(String name, JSArray parameters) | 可能会返回JSObject、JSArray、JSFunction，默认返回null |
| void executeVoidFunction(String name, JSArray parameters) | 没有返回值 |
| Object executeFunction2(String name, Object... parameters) | 可能返回Integer、Double、Boolean、String、JSArray、JSObject、JSFunction、null，入参为java数组，仅支持Integer、Double、Boolean、String、JSArray、JSObject、JSFunction、null |
| boolean contains(String key) | 是否包含该字段 |
| String[] getKeys() | 获取属性列表 |


#### JSArray

继承JSObject

| 方法                                                         | 说明              |
| ------------------------------------------------------------ | ----------------- |
| push(value)                                                  | 设置属性，支持int、boolean、double、String、JSValue |
| int getInteger(String key) | 返回值int对象值，如果没有就会返回0 |
| boolean getBoolean(String key) | 返回值boolean对象值，如果没有就会返回false |
| double getDouble(String key) | 返回值double对象值，如果没有就会返回0 |
| String getString(String key) | 返回值String对象值，如果没有就会返回null |
| JSArray getArray(String key) | 返回值JSArray对象值，如果没有就会返回null |
| JSObject getObject(String key) | 可能会返回JSObject、JSArray、JSFunction，如果没有就会返回null |
| length()                                                     | 数组大小  |

#### JSFunction

继承JSObject

| 方法                                                         | 说明     |
| ------------------------------------------------------------ | -------- |
| JSFunction(JSContext context, JavaCallback callback)         | 构造函数 |
| JSFunction(JSContext context, JavaVoidCallback callback)     | 构造函数 |
| Object call(JSValue.TYPE type, JSObject receiver, JSArray parameters) | 调用方法 |

#### JSContext

继承JSObject，拥有JSObject全部方法，对象本身是全局对象

| 方法                                                         | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| void close()                                                 | 销毁上下文                                                   |
| int executeIntegerScript(String source, String fileName)     | 执行js脚本                                                   |
| double executeDoubleScript(String source, String fileName)   | 执行js脚本                                                   |
| String executeStringScript(String source, String fileName)   | 执行js脚本                                                   |
| boolean executeBooleanScript(String source, String fileName) | 执行js脚本                                                   |
| Object executeScript(String source, String fileName)         | 执行js脚本，可能返回Integer、Double、Boolean、String、JSArray、JSObject、JSFunction、null |
| void executeVoidScript(String source, String fileName)       | 执行js脚本，无返回值                                         |
| JSArray executeArrayScript(String source, String fileName)   | 执行js脚本，返回值为JSArray                                  |
| JSObject executeObjectScript(String source, String fileName) | 执行js脚本，可能会返回JSObject、JSArray、JSFunction          |





