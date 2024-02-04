package com.yzq.logger.file

import com.yzq.logger.common.LogType


/**
 * @description: 文件日志常量类，用于存放文件日志相关的常量信息
 * @author : yuzhiqiang
 */

object FileLogConstant {
    //日志文件的存储目录
    const val dirName: String = ".log"

    //日志文件的存储时长，单位小时，超过这个时间的日志文件将被删除,小于等于0表示不清除
    const val storageDuration: Int = 1 * 24 * 7

    //日志文件的最大大小，单位字节，超过这个大小的后会轮转日志文件。默认10M
    const val maxFileSize: Int = 1 * 1024 * 1024 * 5

//    //日志文件的间隔时间，单位小时，超过这个时间后会轮转日志文件
//    var rotationInterval: Int = 1

    //日志文件名字的前缀
    const val filePrefix: String = "log"

    //最低日志级别
    val minLevel: LogType = LogType.VERBOSE

    //日志阻塞队列的最大容量
    const val logQueueCapacity: Int = 1000

    //主动触发日志写入的间隔时间，单位秒
    const val writeLogInterval: Long = 10

    //写入前存放在内存中的最大日志条数
    const val maxCacheSize: Int = 500
}