package com.yzq.logger

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener


/**
 * @description 日志打印
 * @author  yuzhiqiang (zhiqiang.yu.xeon@gmail.com)
 */

object Logger {

    //配置类
    var config: LogConfig = LogConfig.Builder().build()

    @JvmStatic
    fun init(logConfig: LogConfig) {
        this.config = logConfig
    }


    @JvmStatic
    fun v(vararg content: Any) {
        vt(config.tag, *content)
    }


    @JvmOverloads
    @JvmStatic
    fun vt(
        tag: String = config.tag,
        vararg content: Any,
    ) {
        print(LogType.VERBOSE, tag, *content)
    }

    @JvmStatic
    fun i(vararg content: Any) {
        it(config.tag, *content)
    }

    @JvmOverloads
    @JvmStatic
    fun it(
        tag: String = config.tag,
        vararg content: Any,
    ) {
        print(LogType.INFO, tag, *content)
    }

    @JvmStatic
    fun d(vararg content: Any) {
        dt(config.tag, *content)
    }

    @JvmOverloads
    @JvmStatic
    fun dt(
        tag: String = config.tag,
        vararg content: Any,
    ) {
        print(LogType.DEBUG, tag, *content)
    }


    @JvmStatic
    fun w(
        vararg content: Any,
    ) {
        wt(config.tag, *content)
    }

    @JvmOverloads
    @JvmStatic
    fun wt(
        tag: String = config.tag,
        vararg content: Any,
    ) {
        print(LogType.WARN, tag, *content)
    }


    @JvmStatic
    fun e(vararg content: Any) {
        et(config.tag, *content)
    }

    @JvmOverloads
    @JvmStatic
    fun et(
        tag: String = config.tag,
        vararg content: Any,
    ) {
        print(LogType.ERROR, tag, *content)
    }


    @JvmStatic
    fun wtf(
        vararg content: Any,
    ) {
        wtft(config.tag, *content)
    }

    @JvmOverloads
    @JvmStatic
    fun wtft(
        tag: String = config.tag,
        vararg content: Any,
    ) {
        print(LogType.WTF, tag, *content)
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
        logType: LogType = LogType.INFO,
        tag: String = config.tag,
        vararg content: Any,
    ) {
        if (!config.enable) {
            return
        }
        //打印日志
        config.printers.forEach {
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
        tag: String = config.tag,
        type: LogType = LogType.INFO,
    ) {
        val tokener = JSONTokener(json)

        val obj = runCatching {
            tokener.nextValue()//解析字符串返回根对象
        }.getOrDefault(" Parse json error")

        val message = when (obj) {
            is JSONObject -> obj.toString(2) //转成格式化的json
            is JSONArray -> obj.toString(2) //转成格式化的json
            else -> obj.toString()
        }

        print(type, tag, message)
    }


}