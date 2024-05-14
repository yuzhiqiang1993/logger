package com.yzq.logger.data

import com.yzq.logger.common.LogType


/**
 * @description: 日志实体类，用于存储日志信息，如tag，日志级别，时间戳，线程信息，堆栈信息，日志内容等信息
 * @author : yuzhiqiang
 */

class FileLogItem(
    val tag: String,
    val logType: LogType,
    var timeMillis: Long,
    var threadName: String? = null,
    var traceInfo: String? = null,
    vararg val content: Any,
)