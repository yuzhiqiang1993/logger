package com.yzq.logger.file

import com.yzq.logger.common.LogType
import com.yzq.logger.core.AbsLogConfig

/**
 * @description: 文件日志配置类 Builder 模式
 * @author: yuzhiqiang
 */
class FileLogConfig private constructor(builder: Builder) :
    AbsLogConfig(builder.enable, builder.tag, builder.showStackTrace, builder.showThreadInfo) {

    // 日志文件的存储目录
    val dirName: String = builder.dirName

    // 日志文件的存储时长，单位小时，超过这个时间的日志文件将被删除，小于等于0表示不清除
    val storageDuration: Int = builder.storageDuration

    // 日志文件的最大大小，单位字节，超过这个大小的后会轮转日志文件
    val maxFileSize: Int = builder.maxFileSize

    // 日志文件名字的前缀
    val filePrefix: String = builder.filePrefix

    // 最低日志级别
    val minLevel: LogType = builder.minLevel

    // 日志阻塞队列的最大容量
    val logCapacity: Int = builder.logCapacity

    // 主动触发日志写入的间隔时间，单位秒
    val writeLogInterval: Long = builder.writeLogInterval

    // 写入前存放在内存中的最大日志条数
    val memoryCacheSize: Int = builder.memoryCacheSize


    class Builder {

        var enable: Boolean = true
            private set

        var tag: String = "FileLog"
            private set

        var showStackTrace: Boolean = true
            private set

        var showThreadInfo: Boolean = true
            private set


        // 日志文件的存储目录
        var dirName: String = FileLogConstant.dirName
            private set

        // 日志文件的存储时长，单位小时，超过这个时间的日志文件将被删除，小于等于0表示不清除
        var storageDuration: Int = FileLogConstant.storageDuration
            private set

        // 日志文件的最大大小，单位字节，超过这个大小的后会轮转日志文件
        var maxFileSize: Int = FileLogConstant.maxFileSize
            private set

        // 日志文件名字的前缀
        var filePrefix: String = FileLogConstant.filePrefix
            private set

        // 最低日志级别
        var minLevel: LogType = LogType.VERBOSE
            private set

        // 日志流的最大容量
        var logCapacity: Int = FileLogConstant.logCapacity
            private set

        // 主动触发日志写入的间隔时间，单位秒
        var writeLogInterval: Long = FileLogConstant.writeLogInterval
            private set

        // 写入前存放在内存中的最大日志条数
        var memoryCacheSize: Int = FileLogConstant.memoryCacheSize
            private set

        fun enable(enable: Boolean) = apply {
            this.enable = enable

        }

        fun tag(tag: String) = apply {
            this.tag = tag

        }

        fun showStackTrace(showStackTrace: Boolean) = apply {
            this.showStackTrace = showStackTrace
        }

        fun showThreadInfo(showThreadInfo: Boolean) = apply {
            this.showThreadInfo = showThreadInfo

        }


        fun dirName(dirName: String) = apply {
            this.dirName = dirName

        }

        fun storageDuration(storageDuration: Int) = apply {
            this.storageDuration = storageDuration

        }

        fun maxFileSize(maxFileSize: Int) = apply {
            this.maxFileSize = maxFileSize

        }

        fun filePrefix(filePrefix: String) = apply {
            this.filePrefix = filePrefix

        }

        fun minLevel(minLevel: LogType) = apply {
            this.minLevel = minLevel

        }

        fun logCapacity(logCapacity: Int) = apply {
            this.logCapacity = logCapacity

        }

        fun writeLogInterval(writeLogInterval: Long) = apply {
            this.writeLogInterval = writeLogInterval

        }

        fun memoryCacheSize(memoryCacheSize: Int) = apply {
            this.memoryCacheSize = memoryCacheSize
        }


        fun build(): FileLogConfig {
            return FileLogConfig(this).apply {

                if (logCapacity < FileLogConstant.minLogCapacity) {
                    throw IllegalArgumentException("logQueueCapacity must be greater than ${FileLogConstant.minLogCapacity}")
                }
                if (writeLogInterval < FileLogConstant.minWriteLogInterval) {
                    throw IllegalArgumentException("writeLogInterval must be greater than ${FileLogConstant.minWriteLogInterval}")
                }

                if (memoryCacheSize < FileLogConstant.minCacheSize) {
                    throw IllegalArgumentException("maxCacheSize must be greater than ${FileLogConstant.minCacheSize}")
                }

                if (dirName.isEmpty()) {
                    throw IllegalArgumentException("dirName must not be empty")
                }

            }
        }
    }
}
