package com.yzq.logger.core

import com.yzq.logger.common.LogType


/**
 * @description: 打印接口
 * @author : yuzhiqiang
 */

abstract class AbsPrinter {
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