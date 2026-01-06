package com.yzq.logger.data

import com.yzq.logger.common.LogType
import java.util.UUID


/**
 * @description: 日志实体类，用于存储日志信息，如tag，日志级别，时间戳，线程信息，堆栈信息，日志内容等信息
 * @author : yuzhiqiang
 */

internal class ViewLogItem(
    val id: String = UUID.randomUUID().toString(),
    val logType: LogType,
    val tag: String,
    val content: String
)
