# logger

小而美的日志打印组件，具备以下功能
1.完善的日志显示，包括tag，线程，代码位置
2.支持json格式化打印
3.提供默认可选打印到控制台，文件，以及可视化界面打印器，支持自定义打印器

#文件打印
1.配置文件log的存储路径
2.配置单个文件log的存储大小，超过大小自动切换文件
3.配置存储文件的时间，超过时间自动切换文件
4.配置文件存留时间，超过时间自动删除文件
5.配置文件打印的格式，包括tag，线程，代码位置，时间，日志内容
6.文件名命名规则，log-2024-01-13-1

# 使用方式

**添加依赖**

```kotlin
implementation("com.xeonyu:logger:1.0.4")
```

**代码示例**

```kotlin
/*添加观察者*/
Logger.addObserver(object : LogObserver {
    override fun log(info: LogInfo) {
        println("log: ${info.msg}")
    }
})

/*打印*/
Logger.v("onCreate")
Logger.d("onCreate")
/*子线程打印*/
thread {
    Logger.i("onCreate")
}
Logger.w("onCreate")
Logger.e("onCreate", tr = Exception("data error"))
/*打印json*/
Logger.json(JSONObject().put("name", "yuzhiqiang").put("age", 18).toString())
```

控制台显示如下：
第一行分别是 TAG，线程名，代码位置
![img.png](assets/img.png)

