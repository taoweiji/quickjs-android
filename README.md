# quickjs-android
[![Download](https://maven-badges.herokuapp.com/maven-central/io.github.taoweiji.quickjs/quickjs-android/badge.svg)](https://search.maven.org/search?q=io.github.taoweiji.quickjs)


quickjs-android 是 QuickJS JavaScript 引擎的 Andoroid 接口框架，实现了 Java 调用内核功能。整体大小仅 500KB，是 Google V8 的不错替代品，启动速度比 V8更快，几乎完美支持[ES2020](https://tc39.github.io/ecma262/)。

> 框架目前还处于开发阶段，切莫在正式项目中使用。

### 使用教程

##### 引入依赖

```groovy
implementation 'io.github.taoweiji.quickjs:quickjs-android:1.0.0'
```

##### 简单示例

```java
QuickJS quickJS = QuickJS.createV8Runtime();
JSContext jsContext = quickJS.createContext();
int result = jsContext.executeIntegerScript("var a = 2+10;\n a;", "file.js");
jsContext.close();
quickJS.close();
```



### 对象介绍

##### QuickJS

运行环境，可以创建多个运行时环境，不同的环境之间不能共享对象

```java
QuickJS quickJS = QuickJS.createV8Runtime();
```

##### JSContext

由 QuickJS 创建，一个 QuickJS 可以创建多个 JSContext。

```
JSContext jsContext = quickJS.createContext();
```

##### JSValue

所有的 JS 类型的基类，比如 int、double、object、array等。

##### JSObject

object 对应的类型

```java
JSObject object = new JSObject(jsContext);
object.set("name", "Wiki");
object.set("age", 18);

Log.e("QuickJS", String.valueOf(object.getString("name")));
Log.e("QuickJS", String.valueOf(object.getInteger("age")));
```
##### JSArray

object 对应的类型

```java
JSArray array = new JSArray(jsContext);
array.push(1);
array.push(3.14);
array.push(true);
array.push("Hello World");

Log.e("QuickJS", String.valueOf(arr.getInteger(0)));
Log.e("QuickJS", String.valueOf(arr.getDouble(1)));
Log.e("QuickJS", String.valueOf(arr.getBoolean(2)));
Log.e("QuickJS", String.valueOf(arr.getString(3)));
Log.e("QuickJS", String.valueOf(arr.length()));
```

##### JSFunction

```
```



### 注入方法

```
```



#### QuickJS

| 方法                             | 说明       |
| -------------------------------- | ---------- |
| static QuickJS createV8Runtime() | 创建运行时 |
| JSContext createContext()        | 创建上下文 |
| void close()                     | 销毁运行时 |

#### JSValue

一切JS元素的基类

| 方法                             | 说明       |
| -------------------------------- | ---------- |
| void close()                     | 销毁运行时 |

#### JSObject

继承JSValue

| 方法                                                         | 说明                                                 |
| ------------------------------------------------------------ | ---------------------------------------------------- |
| set(key, value)                                              | 设置属性                                             |
| getInteger/getBoolean/getDouble/getString/getArray/getObject | 提供了6个获取方法                                    |
| registerJavaMethod(JavaCallback callback, String jsFunctionName) | 注册JS函数，调用函数会执行java的Callback，带有返回值 |
| registerJavaMethod(JavaVoidCallback callback, String jsFunctionName) | 注册JS函数，调用函数会执行java的Callback，不带返回值 |
| executeFunction                                              | 一共提供了10个相关的方法，用于执行JS函数获取返回值   |
| boolean contains(String key)                                 | 判断是否包含属性                                     |
| String[] getKeys()                                           | 获取所有的属性名称                                   |
|                                                              |                                                      |
|                                                              |                                                      |

#### JSArray

继承JSObject

| 方法                                                         | 说明              |
| ------------------------------------------------------------ | ----------------- |
| push(value)                                                  | 设置属性          |
| getInteger/getBoolean/getDouble/getString/getArray/getObject | 提供了6个获取方法 |
| length()                                                     | 数组大小  |

#### JSFunction

继承JSObject

| 方法                                                         | 说明              |
| ------------------------------------------------------------ | ----------------- |
| push(value)                                                  | 设置属性          |
| getInteger/getBoolean/getDouble/getString/getArray/getObject | 提供了6个获取方法 |
| length()                                                     | 数组大小          |

#### JSContext

继承JSObject，拥有JSObject全部方法，对象本身是全局对象

| 方法                                                         | 说明       |
| ------------------------------------------------------------ | ---------- |
| void close()                                                 | 销毁上下文 |
| int executeIntegerScript(String source, String fileName)     |            |
| double executeDoubleScript(String source, String fileName)   |            |
| String executeStringScript(String source, String fileName)   |            |
| boolean executeBooleanScript(String source, String fileName) |            |
| Object executeScript(String source, String fileName)         |            |
| void executeVoidScript(String source, String fileName)       |            |
| JSArray executeArrayScript(String source, String fileName)   |            |
|                                                              |            |



## TODO 

- 增加NULL和未定义

















