package com.yzq.logger.printer

import com.yzq.logger.LogType


/**
 * @description: 打印接口
 * @author : yuzhiqiang
 */

interface IPrinter {
    fun print(
        logType: LogType,
        tag: String,
        vararg content: Any,
    )
}