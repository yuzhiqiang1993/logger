package com.yzq.logger.view.core

import com.yzq.logger.common.LogType
import com.yzq.logger.common.firstStackTraceInfo
import com.yzq.logger.common.formatLogContent
import com.yzq.logger.common.formatLogHeader
import com.yzq.logger.core.ILogFormatter


/**
 * @description: 视图格格式化器
 * @author : yuzhiqiang
 */
internal object ViewLogFormatter :
    ILogFormatter {


    /**
     * 格式化日志内容并返回tag
     * @param logType LogType
     * @param tag String
     * @param content Array<out Any>
     * @return Pair<String, String> (tag, formattedContent)
     */
    fun formatWithTag(logType: LogType, tag: String, vararg content: Any): Pair<String, String> {
        val contentStr = parseContent(*content)

        val threadName =
            InternalViewLogConfig.showThreadInfo.takeIf { it }?.let { Thread.currentThread().name }
        val traceInfo =
            InternalViewLogConfig.showStackTrace.takeIf { it }
                ?.let { Throwable().firstStackTraceInfo() }

        val formattedContent = buildLogStr(tag, contentStr, threadName, System.currentTimeMillis(), traceInfo)
        return Pair(tag, formattedContent)
    }

    /**
     * 格式化日志内容
     * @param logType LogType
     * @param tag String
     * @param content Array<out Any>
     * @return String
     */
    override fun formatToStr(logType: LogType, tag: String, vararg content: Any): String {
        return formatWithTag(logType, tag, *content).second
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
        sb.appendLine(
            formatLogHeader(
                tag, threadName = threadName, timeMillis = timeMillis, traceInfo = traceInfo
            )
        )
        sb.appendLine(formatLogContent(contentStr))

        return sb.toString().trim()
    }


}