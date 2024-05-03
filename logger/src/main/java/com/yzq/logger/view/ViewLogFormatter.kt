package com.yzq.logger.view

import com.yzq.logger.common.LogType
import com.yzq.logger.common.firstStackTraceInfo
import com.yzq.logger.common.formatLogContent
import com.yzq.logger.common.formatLogHeader
import com.yzq.logger.core.ILogFormatter


/**
 * @description: 视图格格式化器
 * @author : yuzhiqiang
 */
internal class ViewLogFormatter private constructor(private val config: ViewLogConfig) :
    ILogFormatter {

    companion object {
        @Volatile
        private var instance: ViewLogFormatter? = null
        fun getInstance(config: ViewLogConfig): ViewLogFormatter {
            return instance ?: synchronized(this) {
                instance ?: ViewLogFormatter(config).also { instance = it }
            }
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
            config.showThreadInfo.takeIf { it }?.let { Thread.currentThread().name }
        val traceInfo =
            config.showStackTrace.takeIf { it }?.let { Throwable().firstStackTraceInfo() }


        return buildLogStr(tag, contentStr, threadName, System.currentTimeMillis(), traceInfo)
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
        if (config.showBorder != false) {
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