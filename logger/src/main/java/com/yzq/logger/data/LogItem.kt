package com.yzq.logger.data

import com.yzq.logger.common.LogType

open class LogItem(
    val tag: String,
    val logType: LogType,
    open var timeMillis: Long? = null,
    open var threadName: String? = null,
    open var traceInfo: String? = null,
    vararg val content: Any,
)