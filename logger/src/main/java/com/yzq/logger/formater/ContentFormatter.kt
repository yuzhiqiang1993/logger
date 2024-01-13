package com.yzq.logger.formater

import android.util.Log
import com.yzq.logger.LoggerConfig
import com.yzq.logger.ext.firstStackTraceInfo


/**
 * @description: 默认的内容格式化器
 * @author : yuzhiqiang
 */
internal class ContentFormatter : IFormatter {
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ContentFormatter()
        }
    }


    /**
     * 格式化内容
     * @param config LoggerConfig
     * @param tag String
     * @param content Array<out Any>
     * @return String
     */
    override fun formatToStr(config: LoggerConfig, tag: String, vararg content: Any): String {
        val contentStr = parseContent(*content)
        val threadName = if (config.showThreadInfo) Thread.currentThread().name else null
        val traceInfo =
            if (config.showStackTrace) Throwable().firstStackTraceInfo() else null
        return buildLogStr(tag, contentStr, threadName, traceInfo, config)
    }

    /**
     * 解析日志内容
     * @param content Array<out Any>
     * @return String
     */
    private fun parseContent(vararg content: Any): String = runCatching {
        content.joinToString(", ") {
            when (it) {
                is String -> it
                is Throwable -> Log.getStackTraceString(it)
                is Map<*, *> -> it.toString()
                is Collection<*> -> it.toString()
                is Array<*> -> it.contentToString()
                else -> it.toString()
            }
        }
    }.onFailure {
        it.printStackTrace()
    }.getOrDefault("内容格式化异常，无法打印实际内容")

    /**
     * 构建日志内容
     * @param tag String
     * @param contentStr String
     * @param threadName String?
     * @param traceInfo String?
     * @return String
     */
    private fun buildLogStr(
        tag: String,
        contentStr: String,
        threadName: String?,
        traceInfo: String?,
        config: LoggerConfig,
    ): String {
        val sb = StringBuilder()
        val separator = System.getProperty("line.separator") ?: "\n"

        if (config.showBorder) {
            sb.appendLine("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
            sb.appendLine("│ ${buildLogHeaderStr(tag, threadName, traceInfo)}")
            sb.appendLine("├───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
            contentStr.split(separator).forEach {
                sb.appendLine("│ $it")
            }
            sb.appendLine("└───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
        } else {

            sb.appendLine(buildLogHeaderStr(tag, threadName, traceInfo))

            contentStr.split(separator).forEach {
                sb.appendLine(it)
            }
        }
        return sb.toString()
    }

    /**
     * 构建日志头部信息
     * @param tag String
     * @param threadName String?
     * @param traceInfo String?
     * @return String
     */
    private fun buildLogHeaderStr(
        tag: String,
        threadName: String?,
        traceInfo: String?
    ): String {
        val sb = StringBuilder()
        sb.append(tag)
        if (threadName != null) {
            sb.append(" | $threadName")
        }
        if (traceInfo != null) {
            sb.append(" | $traceInfo")
        }

        return sb.toString()
    }


}