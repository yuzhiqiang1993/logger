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


        /*打印*/
        Logger.v("MainActivity", "onCreate")
        Logger.d("MainActivity", "onCreate")
        /*子线程打印*/
        thread {
            Logger.i("MainActivity", "onCreate")
        }
        Logger.w("MainActivity", "111", 23132, arrayListOf("21312", 21131), mapOf(1 to 1, 3 to 3))
        Logger.e("MainActivity", 111, true, Exception("test error"))

        val user = User("yuzhiqiang", 18)

        Logger.i("user", user)

        val userList: MutableList<User> = arrayListOf()
        for (i in 0..4) {
            userList.add(user)
        }
        val userListJson = MoshiUtils.toJson(userList, "  ")

        Logger.i("MainActivity", userListJson)
//        Log.i("userList", userListJson)


        /*打印json*/
        Logger.json("MainActivity", JSONObject().put("name", "yuzhiqiang").put("age", 18).toString())

        viewbinding.btnSkip.setOnClickListener {
            startActivity(JavaActivity.createIntent(this))
        }

        viewbinding.btnPrint.setOnClickListener {
            Logger.i("MainActivity", "点击了打印按钮")

            for (i in 0..100) {
                Logger.i("MainActivity", "循环打印$i")
            }

        }

        viewbinding.btnLogView.setOnClickListener {
//            val intent = Intent(this, LogViewActivityOptimized::class.java)
//            startActivity(intent)

            Logger.showLogInfoPage()
        }


        //循环打印1000次
//        for (i in 0..1000) {
//            Logger.i("循环打印$i")
//        }

        intent


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}