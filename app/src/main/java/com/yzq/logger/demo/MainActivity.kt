package com.yzq.logger.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yzq.logger.LogInfo
import com.yzq.logger.LogObserver
import com.yzq.logger.Logger
import com.yzq.logger.demo.databinding.ActivityMainBinding
import org.json.JSONObject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
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

        viewbinding.btnSkip.setOnClickListener {
            startActivity(JavaActivity.createIntent(this))
        }

    }
}