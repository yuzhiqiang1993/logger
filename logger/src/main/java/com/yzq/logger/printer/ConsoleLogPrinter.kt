package com.yzq.logger.printer

import com.yzq.logger.LogType
import com.yzq.logger.ext.doLog
import com.yzq.logger.formater.ContentFormatter
import com.yzq.logger.formater.IFormatter
import kotlin.math.min


/**
 * @description: 打印到控制台
 * @author : yuzhiqiang
 */
class ConsoleLogPrinter private constructor() : AbsPrinter() {

    private var formatter: IFormatter = ContentFormatter.instance

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ConsoleLogPrinter()
        }
    }


    override fun print(
        logType: LogType,
        tag: String?,
        vararg content: Any
    ) {
        if (!loggerConfig.enable) return
        val finalTag = tag ?: loggerConfig.tag
        //格式化后的内容
        val logStr = formatter.formatToStr(loggerConfig, finalTag, *content)
        val max = if (loggerConfig.lineLength >= 500) loggerConfig.lineLength else 500
        val length = logStr.length
        //显示到控制台
        if (length > max) {
            //多行显示
            synchronized(this) {
                //这里使用StringBuilder来拼接字符串，避免频繁创建String对象
                val logBuilder = StringBuilder()
                var startIndex = 0
                var endIndex = max
                while (startIndex < length) {
                    endIndex = min(length, endIndex)//1500
                    logBuilder.append(logStr, startIndex, endIndex)
                    doLog(logType, finalTag, logBuilder.toString())
                    logBuilder.clear()
                    startIndex += max
                    endIndex += startIndex + max
                }
            }
        } else {
            //单行显示
            doLog(logType, finalTag, logStr)
        }
    }
}