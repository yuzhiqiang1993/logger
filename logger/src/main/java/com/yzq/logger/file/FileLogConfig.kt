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
    val logQueueCapacity: Int = builder.logQueueCapacity

    // 主动触发日志写入的间隔时间，单位秒
    val writeLogInterval: Long = builder.writeLogInterval

    // 写入前存放在内存中的最大日志条数
    val maxCacheSize: Int = builder.maxCacheSize


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

        // 日志阻塞队列的最大容量
        var logQueueCapacity: Int = FileLogConstant.logQueueCapacity
            private set

        // 主动触发日志写入的间隔时间，单位秒
        var writeLogInterval: Long = FileLogConstant.writeLogInterval
            private set

        // 写入前存放在内存中的最大日志条数
        var maxCacheSize: Int = FileLogConstant.maxCacheSize
            private set

        fun enable(enable: Boolean): Builder {
            this.enable = enable
            return this
        }

        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun showStackTrace(showStackTrace: Boolean): Builder {
            this.showStackTrace = showStackTrace
            return this
        }

        fun showThreadInfo(showThreadInfo: Boolean): Builder {
            this.showThreadInfo = showThreadInfo
            return this
        }


        fun dirName(dirName: String): Builder {
            this.dirName = dirName
            return this
        }

        fun storageDuration(storageDuration: Int): Builder {
            this.storageDuration = storageDuration
            return this
        }

        fun maxFileSize(maxFileSize: Int): Builder {
            this.maxFileSize = maxFileSize
            return this
        }

        fun filePrefix(filePrefix: String): Builder {
            this.filePrefix = filePrefix
            return this
        }

        fun minLevel(minLevel: LogType): Builder {
            this.minLevel = minLevel
            return this
        }

        fun logQueueCapacity(logQueueCapacity: Int): Builder {
            this.logQueueCapacity = logQueueCapacity
            return this
        }

        fun writeLogInterval(writeLogInterval: Long): Builder {
            this.writeLogInterval = writeLogInterval
            return this
        }

        fun maxCacheSize(maxCacheSize: Int): Builder {
            this.maxCacheSize = maxCacheSize
            return this
        }


        fun build(): FileLogConfig {
            return FileLogConfig(this).apply {

                if (logQueueCapacity <= FileLogConstant.minLogQueueCapacity) {
                    throw IllegalArgumentException("logQueueCapacity must be greater than ${FileLogConstant.minLogQueueCapacity}")
                }
                if (writeLogInterval < FileLogConstant.minWriteLogInterval) {
                    throw IllegalArgumentException("writeLogInterval must be greater than ${FileLogConstant.minWriteLogInterval}")
                }

                if (maxCacheSize <= FileLogConstant.minCacheSize) {
                    throw IllegalArgumentException("maxCacheSize must be greater than ${FileLogConstant.minCacheSize}")
                }

                if (dirName.isEmpty()) {
                    throw IllegalArgumentException("dirName must not be empty")
                }


            }
        }
    }
}
