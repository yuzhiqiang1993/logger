package com.yzq.logger.printer

import com.yzq.logger.LogType
import com.yzq.logger.LoggerConfig


/**
 * @description: 打印接口
 * @author : yuzhiqiang
 */

abstract class AbsPrinter {

    var loggerConfig: LoggerConfig = LoggerConfig.Builder().build()

    /**
     * 打印
     * @param logType LogType
     * @param tag String
     * @param content Array<out Any>
     */
    abstract fun print(
        logType: LogType,
        tag: String?,
        vararg content: Any,
    )
}