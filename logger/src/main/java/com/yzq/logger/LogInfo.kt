package com.yzq.logger


/**
 * 打印的日志信息
 * @property type Type
 * @property msg String
 * @property tag String
 * @property throwable Throwable?
 * @constructor
 */
data class LogInfo(
    var type: Logger.Type,
    var msg: String,
    var tag: String,
    var throwable: Throwable?,
)