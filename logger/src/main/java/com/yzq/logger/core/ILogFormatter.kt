package com.yzq.logger.core

import android.util.Log
import com.yzq.logger.common.LogType
import com.yzq.logger.data.LogItem


/**
 * @description: 格式化接口
 * @author : yuzhiqiang
 */

interface ILogFormatter {
    fun formatToStr(logType: LogType, tag: String, vararg content: Any): String {
        return parseContent(*content)
    }

    fun formatToStr(logItem: LogItem): String {
        return formatToStr(logItem.logType, logItem.tag, *logItem.content)
    }


    /**
     * 解析日志内容
     * @param content Array<out Any>
     * @return String
     */
    fun parseContent(vararg content: Any?): String = runCatching {
        content.joinToString(", ") {
            when (it) {
                is String -> it
                is Throwable -> Log.getStackTraceString(it)
                is Map<*, *> -> it.toString()
                is Collection<*> -> it.toString()
                is Array<*> -> it.contentToString()
                else -> it?.toString() ?: "null"
            }
        }
    }.onFailure {
        it.printStackTrace()
    }.getOrDefault("Logger:内容解析异常，无法打印实际内容")
}

