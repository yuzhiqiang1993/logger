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

    // 使用 ThreadLocal 复用 StringBuilder，避免频繁创建
    private val threadLocalBuilder = object : ThreadLocal<StringBuilder>() {
        override fun initialValue(): StringBuilder = StringBuilder(4096)
    }

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
        val finalTag = tag.ifEmpty {
            InternalConsoleConfig.tag
        }
        // 格式化后的内容
        val logStr = ConsoleLogFormatter.formatToStr(logType, finalTag, *content)

        // 控制台最大显示长度，必须在 500 到 4000 之间
        val max = InternalConsoleConfig.lineLength.coerceAtLeast(500).coerceAtMost(4000)

        val length = logStr.length
        // 显示到控制台
        if (length > max) {
            // 多行显示：先准备好所有分段（在锁外）
            val segments = mutableListOf<String>()
            val logBuilder = threadLocalBuilder.get()!!
            logBuilder.clear()
            
            var startIndex = 0
            while (startIndex < length) {
                val endIndex = min(length, startIndex + max)
                logBuilder.append(logStr, startIndex, endIndex)
                segments.add(logBuilder.toString())
                logBuilder.clear()
                startIndex = endIndex
            }
            
            // 只锁住打印操作，确保日志顺序（锁粒度更小）
            synchronized(this) {
                segments.forEach { segment ->
                    doLog(logType, finalTag, segment)
                }
            }
        } else {
            // 单行显示
            doLog(logType, finalTag, logStr)
        }
    }
}