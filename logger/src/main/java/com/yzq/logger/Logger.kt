package com.yzq.logger

import com.yzq.logger.base.AbsPrinter
import com.yzq.logger.common.LogType
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

    fun addPrinter(printer: AbsPrinter): Logger {
        if (printerList.contains(printer)) {
            return this
        }
        printerList.add(printer)
        return this
    }

    fun removePrinter(printer: AbsPrinter) {
        if (printerList.contains(printer)) {
            printerList.remove(printer)
        }
    }

    fun clearPrinter() {
        printerList.clear()
    }


    @JvmStatic
    fun v(vararg content: Any) {
        print(LogType.VERBOSE, content = content)
    }


    @JvmStatic
    fun vt(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.VERBOSE, tag, content)
    }

    @JvmStatic
    fun i(vararg content: Any) {
        print(LogType.INFO, content = content)
    }

    @JvmStatic
    fun it(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.INFO, tag, *content)
    }

    @JvmStatic
    fun d(vararg content: Any) {
        print(LogType.DEBUG, content = content)
    }

    @JvmStatic
    fun dt(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.DEBUG, tag, *content)
    }


    @JvmStatic
    fun w(
        vararg content: Any,
    ) {
        print(LogType.WARN, content = content)
    }

    @JvmStatic
    fun wt(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.WARN, tag, *content)
    }


    @JvmStatic
    fun e(vararg content: Any) {
        print(LogType.ERROR, content = content)
    }

    @JvmStatic
    fun et(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.ERROR, tag, content)
    }


    @JvmStatic
    fun wtf(
        vararg content: Any,
    ) {
        print(LogType.WTF, content = content)
    }

    @JvmStatic
    fun wtft(
        tag: String,
        vararg content: Any,
    ) {
        print(LogType.WTF, tag, content)
    }


    /**
     * 输出日志
     * 如果[msg]和[tr]为空或者[tag]为空将不会输出日志, 拦截器
     *
     * @param logType 日志等级
     * @param msg 日志信息
     * @param tag 日志标签
     * @param occurred 日志异常
     */
    private fun print(
        logType: LogType,
        tag: String? = null,
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
        json: String,
        tag: String? = null,
        type: LogType = LogType.INFO,
    ) {
        val tokener = JSONTokener(json)

        val obj = runCatching {
            tokener.nextValue()//解析字符串返回根对象
        }.onFailure {
            it.printStackTrace()
        }.getOrDefault("Parse json error")

        val message = when (obj) {
            is JSONObject -> obj.toString(2) //转成格式化的json
            is JSONArray -> obj.toString(2) //转成格式化的json
            else -> obj.toString()
        }

        print(type, tag, message)
    }


}