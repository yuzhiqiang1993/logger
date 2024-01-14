package com.yzq.logger.console

import com.yzq.logger.base.ILogFormatter
import com.yzq.logger.common.LogType
import com.yzq.logger.common.firstStackTraceInfo
import com.yzq.logger.common.formatLogContent
import com.yzq.logger.common.formatLogHeader


/**
 * @description: 默认的内容格式化器
 * @author : yuzhiqiang
 */
internal class ConsoleLogFormatter private constructor() : ILogFormatter {

    var config: ConsoleLogConfig? = null

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ConsoleLogFormatter()
        }
    }


    /**
     * 格式化日志内容
     * @param logType LogType
     * @param tag String
     * @param content Array<out Any>
     * @return String
     */
    override fun formatToStr(logType: LogType, tag: String, vararg content: Any): String {
        val contentStr = parseContent(*content)

        val threadName =
            config?.showThreadInfo.takeIf { it == true }?.let { Thread.currentThread().name }
        val traceInfo =
            config?.showStackTrace.takeIf { it == true }?.let { Throwable().firstStackTraceInfo() }
        val timeMillis =
            config?.showTimestamp.takeIf { it == true }?.let { System.currentTimeMillis() }

        return buildLogStr(tag, contentStr, threadName, timeMillis, traceInfo)
    }


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
        timeMillis: Long?,
        traceInfo: String?,
    ): String {
        val sb = StringBuilder()
        if (config?.showBorder != false) {
            sb.appendLine("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
            sb.appendLine(
                "│ ${
                    formatLogHeader(
                        tag,
                        threadName = threadName,
                        timeMillis = timeMillis,
                        traceInfo = traceInfo
                    )
                }"
            )
            sb.appendLine("├───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
//            sb.append(formatLogContent(contentStr, linePrefix = "│ "))
            sb.append(formatLogContent(contentStr))
            sb.appendLine("└───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
        } else {
            sb.appendLine(
                formatLogHeader(
                    tag,
                    threadName = threadName,
                    timeMillis = timeMillis,
                    traceInfo = traceInfo
                )
            )
            sb.appendLine(formatLogContent(contentStr))
        }
        return sb.toString()
    }


}