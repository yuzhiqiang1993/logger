package com.yzq.logger.file

import com.yzq.logger.common.LogType
import com.yzq.logger.core.AbsLogConfig

/**
 * @description: 文件日志配置类 Builder 模式
 * @author: yuzhiqiang
 */
object InternalFileLogConfig : AbsLogConfig() {

    // 日志文件的存储目录
    var dirName: String = FileLogConstant.dirName

    // 日志文件的存储时长，单位小时，超过这个时间的日志文件将被删除，小于等于0表示不清除
    var storageDuration: Int = FileLogConstant.storageDuration

    // 日志文件的最大大小，单位字节，超过这个大小的后会轮转日志文件
    var maxFileSize: Int = FileLogConstant.maxFileSize

    // 日志文件名字的前缀
    var filePrefix: String = FileLogConstant.filePrefix

    // 最低日志级别
    var minLevel: LogType = LogType.VERBOSE

    // 日志阻塞队列的最大容量
    var logCapacity: Int = FileLogConstant.logCapacity

    // 主动触发日志写入的间隔时间，单位秒
    var writeLogInterval: Long = FileLogConstant.writeLogInterval

    // 写入前存放在内存中的最大日志条数
    var memoryCacheSize: Int = FileLogConstant.memoryCacheSize


    fun apply(config: FileLogConfig) {
        enable = config.enable
        tag = config.tag
        showStackTrace = config.showStackTrace
        showThreadInfo = config.showThreadInfo
        dirName = config.dirName
        storageDuration = config.storageDuration
        maxFileSize = config.maxFileSize
        filePrefix = config.filePrefix
        minLevel = config.minLevel
        logCapacity = config.logCapacity
        writeLogInterval = config.writeLogInterval
        memoryCacheSize = config.memoryCacheSize
    }
}
