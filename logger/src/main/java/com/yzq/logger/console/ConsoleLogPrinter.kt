package com.yzq.logger.console

import com.yzq.logger.common.LogType
import com.yzq.logger.common.doLog
import com.yzq.logger.core.AbsPrinter
import kotlin.math.min


/**
 * @description: 打印到控制台
 * @author : yuzhiqiang
 */
class ConsoleLogPrinter private constructor() : AbsPrinter() {


    companion object {

        @Volatile
        private var instance: ConsoleLogPrinter? = null
        fun getInstance(
            config: ConsoleLogConfig = ConsoleLogConfig.Builder().build()
        ): ConsoleLogPrinter {
            return instance ?: synchronized(this) {
                instance ?: ConsoleLogPrinter().also {
                    InternalConsoleConfig.apply(config)
                    instance = it
                }
            }

        }
    }


    override fun print(
        logType: LogType, tag: String, vararg content: Any
    ) {

        if (!InternalConsoleConfig.enable) return
        val finalTag = if (tag.isEmpty()) InternalConsoleConfig.tag else tag
        //格式化后的内容
        val logStr = ConsoleLogFormatter.formatToStr(logType, finalTag, *content)

        //控制台最大显示长度,必须在500到4000之间
        val max = InternalConsoleConfig.lineLength.coerceAtLeast(500).coerceAtMost(4000)

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
                    endIndex = min(length, endIndex)
                    logBuilder.append(logStr, startIndex, endIndex)
                    doLog(logType, finalTag, logBuilder.toString())
                    logBuilder.clear()
                    startIndex += max
                    endIndex = startIndex + max
                }
            }
        } else {
            //单行显示
            doLog(logType, finalTag, logStr)
        }
    }
}