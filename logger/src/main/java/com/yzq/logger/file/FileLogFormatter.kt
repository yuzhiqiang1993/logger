package com.yzq.logger.file

import com.yzq.logger.common.formatLogContent
import com.yzq.logger.common.formatLogHeader
import com.yzq.logger.core.ILogFormatter
import com.yzq.logger.data.LogItem


/**
 * @description: 文件日志格式化器
 * @author : yuzhiqiang
 */

internal class FileLogFormatter private constructor() : ILogFormatter {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FileLogFormatter()
        }
    }

    override fun formatToStr(logItem: LogItem): String {
        val sb = StringBuilder()
            .appendLine(
                formatLogHeader(
                    logItem.tag,
                    logItem.traceInfo,
                    logItem.logType,
                    logItem.timeMillis,
                    logItem.threadName
                )
            )
            .appendLine(formatLogContent(parseContent(*logItem.content)))

        return sb.toString()
    }


}