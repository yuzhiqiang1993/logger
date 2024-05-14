package com.yzq.logger.data

import com.yzq.logger.common.LogType

/**
 * @description: 日志类型
 * @author : yuzhiqiang
 */

data class LogTypeItem(val logType: LogType, val type: String, var selected: Boolean = false)