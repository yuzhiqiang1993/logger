package com.yzq.logger

import android.content.Intent
import com.yzq.application.AppContext
import com.yzq.logger.common.LogType
import com.yzq.logger.core.AbsPrinter
import com.yzq.logger.core.loggerDebug
import com.yzq.logger.view.log_view.LogViewActivity
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.util.Collections


/**
 * @description 日志打印
 * @author  yuzhiqiang (zhiqiang.yu.xeon@gmail.com)
 */

object Logger {

    private val printerList = Collections.synchronizedList(arrayListOf<AbsPrinter>())


    fun debug(debug: Boolean): Logger {
        loggerDebug = debug
        return this
    }

    @JvmStatic
    fun addPrinter(printer: AbsPrinter): Logger {
        if (printerList.contains(printer)) {
            return this
        }
        printerList.add(printer)
        return this
    }

    @JvmStatic
    fun removePrinter(printer: AbsPrinter) {
        if (printerList.contains(printer)) {
            printerList.remove(printer)
        }
    }

    @JvmStatic
    fun clearPrinter() {
        printerList.clear()
    }


    @JvmStatic
    fun v(tag: String, vararg content: Any) {
        print(LogType.VERBOSE, tag, *content)
    }


    @JvmStatic
    fun i(tag: String, vararg content: Any) {
        print(LogType.INFO, tag, *content)
    }


    @JvmStatic
    fun d(tag: String, vararg content: Any) {
        print(LogType.DEBUG, tag, *content)
    }

    @JvmStatic
    fun w(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.WARN, tag, *content)
    }


    @JvmStatic
    fun e(tag: String, vararg content: Any) {
        print(LogType.ERROR, tag, *content)
    }


    @JvmStatic
    fun wtf(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.WTF, tag, *content)
    }

    /**
     * 输出日志
     *
     * @param logType 日志等级
     * @param msg 日志信息
     * @param tag 日志标签
     * @param occurred 日志异常
     */
    private fun print(
        logType: LogType,
        tag: String,
        vararg content: Any,
    ) {

        if (printerList.isEmpty() || content.isEmpty()) {
            return
        }
        //打印日志
        printerList.forEach {
            it.print(logType, tag, *content)
        }
    }


    /**
     * 打印json
     * @param json String
     * @param tag String
     * @param type Type
     */
    @JvmOverloads
    @JvmStatic
    fun json(
        tag: String,
        json: String,
        type: LogType = LogType.INFO,
    ) {
        formatJson(json)?.let {
            print(type, tag, it)
        }
    }


    private fun formatJson(json: String): String? {
        val tokenizer = JSONTokener(json)

        val obj = runCatching {
            tokenizer.nextValue()//解析字符串返回根对象
        }.onFailure {
            it.printStackTrace()
        }.getOrDefault("Logger：Json解析异常，无法打印出实际内容")

        val formatJson = when (obj) {
            is JSONObject -> obj.toString(2) //转成格式化的json
            is JSONArray -> obj.toString(2) //转成格式化的json
            else -> obj.toString()
        }
        return formatJson
    }


    fun showLogInfoPage() {
        Intent(AppContext, LogViewActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            AppContext.startActivity(this)
        }
    }

}