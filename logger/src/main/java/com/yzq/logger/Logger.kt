package com.yzq.logger

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.Thread.currentThread
import kotlin.math.min


/**
 * @description 日志打印
 * @author  yuzhiqiang (zhiqiang.yu.xeon@gmail.com)
 */

object Logger {

    enum class Type {
        VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
    }

    /* 当前平台的换行符 */
    private val lineSeparator = System.getProperty("line.separator")

    /** 日志默认标签 */
    private var tag = "Logger"

    /** 是否启用日志 */
    private var enabled = true

    /**
     * 日志的观察者
     */
    private val logObservers by lazy { arrayListOf<LogObserver>() }

    /**
     * @param enabled 是否启用日志
     * @param tag 日志默认标签
     */
    @JvmOverloads
    @JvmStatic
    fun setDebug(enabled: Boolean, tag: String = Logger.tag) {
        Logger.enabled = enabled
        Logger.tag = tag
    }


    /**
     * 添加日志拦截器
     * @param observer LogObserver
     */
    @JvmStatic
    fun addObserver(observer: LogObserver) {
        logObservers.add(observer)
    }

    /**
     * 删除日志拦截器
     */
    @JvmStatic
    fun removeObserver(observer: LogObserver) {
        logObservers.remove(observer)
    }

    /**
     * 清空日志拦截器
     */
    fun clearObserver() {
        logObservers.clear()
    }

    @JvmOverloads
    @JvmStatic
    fun v(
        msg: String,
        tag: String = Logger.tag,
        tr: Throwable? = null,
    ) {

        print(Type.VERBOSE, msg, tag, tr)
    }

    @JvmOverloads
    @JvmStatic
    fun i(
        msg: String,
        tag: String = Logger.tag,
        tr: Throwable? = null,
    ) {
        print(Type.INFO, msg, tag, tr)
    }

    @JvmOverloads
    @JvmStatic
    fun d(
        msg: String,
        tag: String = Logger.tag,
        tr: Throwable? = null,
    ) {
        print(Type.DEBUG, msg, tag, tr)
    }

    @JvmOverloads
    @JvmStatic
    fun w(
        msg: String,
        tag: String = Logger.tag,
        tr: Throwable? = null,
    ) {
        print(Type.WARN, msg, tag, tr)
    }

    @JvmOverloads
    @JvmStatic
    fun e(
        msg: String,
        tag: String = Logger.tag,
        tr: Throwable? = null,
    ) {
        print(Type.ERROR, msg, tag, tr)
    }


    @JvmOverloads
    @JvmStatic
    fun wtf(
        msg: String,
        tag: String = Logger.tag,
        tr: Throwable? = null,
    ) {
        print(Type.WTF, msg, tag, tr)
    }

    /**
     * 输出日志
     * 如果[msg]和[occurred]为空或者[tag]为空将不会输出日志, 拦截器
     *
     * @param type 日志等级
     * @param msg 日志信息
     * @param tag 日志标签
     * @param occurred 日志异常
     */
    private fun print(
        type: Type = Type.INFO,
        msg: String,
        tag: String = Logger.tag,
        tr: Throwable? = null,

        ) {
        if (!enabled) return

        var traceInfo = ""
        /*找出第一个符合条件的堆栈信息*/
        Throwable().stackTrace.firstOrNull {
            it.fileName != "Logger.kt" && it.fileName != null
        }?.run {
            traceInfo = "${className}.$methodName($fileName:$lineNumber)"
        }
        val info = LogInfo(type, msg, tag, tr)
        for (logHook in logObservers) {
            logHook.log(info)
        }

        val sb = StringBuilder()
        sb.appendLine("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
        sb.appendLine("│ $tag | ${currentThread().name} | ${traceInfo}")
        sb.appendLine("├───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
        msg.toString().split(lineSeparator).forEach {
            sb.appendLine("│ $it")
        }
        sb.appendLine("└───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")


        val message = sb.toString()
        val max = 3800
        val length = message.length
        if (length > max) {
            synchronized(this) {
                var startIndex = 0
                var endIndex = max
                while (startIndex < length) {
                    endIndex = min(length, endIndex)
                    val substring = message.substring(startIndex, endIndex)
                    log(type, substring, tag, tr)
                    startIndex += max
                    endIndex += max
                }
            }
        } else {
            log(type, message, tag, tr)
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
        tag: String = Logger.tag,
        type: Type = Type.INFO,
    ) {
        if (!enabled) return

        val tokener = JSONTokener(json)

        val obj = runCatching {
            tokener.nextValue()//解析字符串返回根对象
        }.getOrDefault(" Parse json error")

        val message = when (obj) {
            is JSONObject -> obj.toString(2) //转成格式化的json
            is JSONArray -> obj.toString(2) //转成格式化的json
            else -> obj.toString()
        }

        print(type, message, tag)
    }

    private fun log(type: Type, msg: String, tag: String, tr: Throwable?) {
        when (type) {
            Type.VERBOSE -> Log.v(tag, msg, tr)
            Type.DEBUG -> Log.d(tag, msg, tr)
            Type.INFO -> Log.i(tag, msg, tr)
            Type.WARN -> Log.w(tag, msg, tr)
            Type.ERROR -> Log.e(tag, msg, tr)
            Type.WTF -> Log.wtf(tag, msg, tr)
        }
    }
}