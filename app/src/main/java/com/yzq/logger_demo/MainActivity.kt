package com.yzq.logger_demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yzq.logger.Logger
import com.yzq.logger.common.LogType
import com.yzq.logger.view.log_view.LogViewActivity
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
        Logger.v("onCreate")
        Logger.d("onCreate")
        /*子线程打印*/
        thread {
            Logger.i("onCreate")
        }
        Logger.w("onCreate", "111", 23132, arrayListOf("21312", 21131), mapOf(1 to 1, 3 to 3))
        Logger.e("onCreate", 111, true, Exception("test error"))
        Logger.et("onCreate", "异常信息")

        val user = User("yuzhiqiang", 18)

        Logger.i("user", user)

        val userList: MutableList<User> = arrayListOf()
        for (i in 0..4) {
            userList.add(user)
        }
        val userListJson = MoshiUtils.toJson(userList, "  ")

        Logger.i(userListJson)
//        Log.i("userList", userListJson)


        /*打印json*/
        Logger.json(
            JSONObject().put("name", "yuzhiqiang").put("age", 18).toString(),
            "onCreate",
            LogType.ERROR
        )

        viewbinding.btnSkip.setOnClickListener {
            startActivity(JavaActivity.createIntent(this))
        }

        viewbinding.btnPrint.setOnClickListener {
            Logger.i("点击了打印按钮")

            for (i in 0..100) {
                Logger.i("循环打印$i")
            }

        }

        viewbinding.btnLogView.setOnClickListener {
            val intent = Intent(this, LogViewActivity::class.java)
            startActivity(intent)
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