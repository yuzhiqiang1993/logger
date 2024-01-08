package com.yzq.logger.printer

import com.yzq.logger.LogType
import com.yzq.logger.Logger
import com.yzq.logger.ext.doLog
import com.yzq.logger.formater.ContentFormatter
import com.yzq.logger.formater.IFormatter
import kotlin.math.min


/**
 * @description: 打印到控制台
 * @author : yuzhiqiang
 */
class ConsoleLogPrinter private constructor() : IPrinter {

    private var formatter: IFormatter = ContentFormatter.instance

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ConsoleLogPrinter()
        }
    }


    override fun print(
        logType: LogType,
        tag: String,
        vararg content: Any
    ) {
        if (!Logger.config.enable) return
        //格式化后的内容
        val logStr = formatter.formatToStr(tag, *content)

        val max = if (Logger.config.lineLength >= 500) Logger.config.lineLength else 500
        val length = logStr.length
        //显示到控制台
        if (length > max) {
            //多行显示
            synchronized(this) {
                var startIndex = 0
                var endIndex = max
                while (startIndex < length) {
                    endIndex = min(length, endIndex)
                    val substring = logStr.substring(startIndex, endIndex)
                    doLog(logType, tag, substring)
                    startIndex += max
                    endIndex += max
                }
            }
        } else {
            //单行显示
            doLog(logType, tag, logStr)
        }

    }


}