package com.yzq.logger.file

import com.yzq.logger.base.AbsLogConfig
import com.yzq.logger.common.LogType


/**
 * @description: 文件日志配置类
 * @author : yuzhiqiang
 */

class FileLogConfig : AbsLogConfig() {
    //日志文件的存储路径
    var logFilePath: String = ""

    //日志文件的存储时长，单位小时，超过这个时间的日志文件将被删除,小于等于0表示不清除
    var storageDuration: Int = 1 * 24 * 7

    //日志文件的最大大小，单位字节，超过这个大小的后会轮转日志文件。默认10M
    var maxFileSize: Int = 1 * 1024 * 1024 * 10

    //日志文件的间隔时间，单位小时，超过这个时间后会轮转日志文件
    var rotationInterval: Int = 1

    //日志文件名字的前缀
    var filePrefix: String = "log"

    //最低日志级别
    var minLevel: LogType = LogType.VERBOSE

    class Builder {
        private val config = FileLogConfig()

        fun enable(enable: Boolean): Builder {
            config.enable = enable
            return this
        }

        fun tag(tag: String): Builder {
            config.tag = tag
            return this
        }

        fun logFilePath(logFilePath: String): Builder {
            config.logFilePath = logFilePath
            return this
        }

        fun storageDuration(storageDuration: Int): Builder {
            config.storageDuration = storageDuration
            return this
        }

        fun maxFileSize(maxFileSize: Int): Builder {
            config.maxFileSize = maxFileSize
            return this
        }

        fun rotationInterval(rotationInterval: Int): Builder {
            config.rotationInterval = rotationInterval
            return this
        }

        fun filePrefix(filePrefix: String): Builder {
            config.filePrefix = filePrefix
            return this
        }

        fun minLevel(minLevel: LogType): Builder {
            config.minLevel = minLevel
            return this
        }

        fun build() = config
    }


}