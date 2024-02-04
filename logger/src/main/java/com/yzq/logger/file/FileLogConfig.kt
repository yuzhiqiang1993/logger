package com.yzq.logger.file

import com.yzq.logger.common.LogType
import com.yzq.logger.core.AbsLogConfig


/**
 * @description: 文件日志配置类
 * @author : yuzhiqiang
 */

class FileLogConfig : AbsLogConfig() {

    //日志文件的存储目录
    var dirName: String = FileLogConstant.dirName

    //日志文件的存储时长，单位小时，超过这个时间的日志文件将被删除,小于等于0表示不清除
    var storageDuration: Int = FileLogConstant.storageDuration

    //日志文件的最大大小，单位字节，超过这个大小的后会轮转日志文件。默认10M
    var maxFileSize: Int = FileLogConstant.maxFileSize


    //日志文件名字的前缀
    var filePrefix: String = FileLogConstant.filePrefix

    //最低日志级别
    var minLevel: LogType = LogType.VERBOSE

    //日志阻塞队列的最大容量
    var logQueueCapacity: Int = FileLogConstant.logQueueCapacity

    //主动触发日志写入的间隔时间，单位秒
    var writeLogInterval: Long = FileLogConstant.writeLogInterval

    //写入前存放在内存中的最大日志条数
    var maxCacheSize: Int = FileLogConstant.maxCacheSize


    fun dirName(dirName: String): FileLogConfig {
        this.dirName = dirName
        return this
    }

    fun storageDuration(storageDuration: Int): FileLogConfig {
        this.storageDuration = storageDuration
        return this
    }

    fun maxFileSize(maxFileSize: Int): FileLogConfig {
        this.maxFileSize = maxFileSize
        return this
    }

    fun filePrefix(filePrefix: String): FileLogConfig {
        this.filePrefix = filePrefix
        return this
    }

    fun minLevel(minLevel: LogType): FileLogConfig {
        this.minLevel = minLevel
        return this
    }

    fun logQueueCapacity(logQueueCapacity: Int): FileLogConfig {
        this.logQueueCapacity = logQueueCapacity
        return this
    }

    fun writeLogInterval(writeLogInterval: Long): FileLogConfig {
        this.writeLogInterval = writeLogInterval
        return this
    }

    fun maxCacheSize(maxCacheSize: Int): FileLogConfig {
        this.maxCacheSize = maxCacheSize
        return this
    }

    fun enable(enable: Boolean): FileLogConfig {
        this.enable = enable
        return this
    }

    fun tag(tag: String): FileLogConfig {
        this.tag = tag
        return this
    }


    fun showStackTrace(showStackTrace: Boolean): FileLogConfig {
        this.showStackTrace = showStackTrace
        return this
    }


    fun showThreadInfo(showThreadInfo: Boolean): FileLogConfig {
        this.showThreadInfo = showThreadInfo
        return this
    }

    fun showTimestamp(showTimestamp: Boolean): FileLogConfig {
        this.showTimestamp = showTimestamp
        return this
    }


}