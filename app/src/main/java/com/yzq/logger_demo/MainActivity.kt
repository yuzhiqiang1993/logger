package com.yzq.logger_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yzq.logger.Logger
import com.yzq.logger_demo.data.User
import com.yzq.logger_demo.databinding.ActivityMainBinding
import org.json.JSONObject
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)

        // 初始化时打印一条简单的日志
        Logger.i("MainActivity", "App 启动完成 (App Launched)")

        viewbinding.btnPrint.setOnClickListener {
            demonstrateLogging()
        }

        viewbinding.btnLogView.setOnClickListener {
            Logger.showLogInfoPage()
        }
    }

    private fun demonstrateLogging() {
        val tag = "DemoTag"

        // 1. 基本打印 (Basic Logging)
        Logger.v(tag, "这是一个 Verbose 日志 (Verbose Log)")
        Logger.d(tag, "这是一个 Debug 日志 (Debug Log)")
        Logger.i(tag, "这是一个 Info 日志 (Info Log)")
        Logger.w(tag, "这是一个 Warn 日志 (Warn Log)")
        Logger.e(tag, "这是一个 Error 日志 (Error Log)")

        // 2. 多参数打印 (Multi-argument Logging)
        Logger.d(tag, "多参数测试", 123, true, 45.6f)

        // 3. 集合与对象打印 (Collections & Objects)
        val user = User("Alice", 25)
        val list = listOf("Apple", "Banana", "Cherry")
        val map = mapOf("Key1" to "Value1", "Key2" to 999)
        
        Logger.i(tag, "打印对象:", user)
        Logger.i(tag, "打印列表:", list)
        Logger.d(tag, "打印 Map:", map)

        // 4. JSON 打印 (JSON Logging)
        val jsonObject = JSONObject()
        jsonObject.put("project", "Logger")
        jsonObject.put("stars", 1000)
        jsonObject.put("features", listOf("Pretty", "Simple", "Powerful").toString())
        Logger.json(tag, jsonObject.toString())

        // 5. 异常打印 (Exception Logging)
        try {
            val result = 1 / 0
        } catch (e: Exception) {
            Logger.e(tag, "捕获到异常 (Exception Caught)", e)
        }

        // 6. 子线程打印 (Thread Logging)
        thread(name = "Background-Thread") {
            Logger.w("ThreadTag", "这是在子线程中打印的日志 (Log from background thread)")
            Thread.sleep(100)
            Logger.d("ThreadTag", "子线程任务结束 (Thread task finished)")
        }
        
        // 7. 批量打印测试 (Batch Test)
        // thread {
        //     repeat(20) {
        //        Logger.v(tag, "批量日志测试序号: $it")
        //        Thread.sleep(10)
        //     }
        // }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}