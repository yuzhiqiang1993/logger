package com.yzq.logger.file


/**
 * @description: 文件日志常量类，用于存放文件日志相关的常量信息
 * @author : yuzhiqiang
 */

internal object FileLogConstant {
    //日志文件的存储目录
    const val dirName: String = ".log"

    //日志文件的存储时长，单位小时，超过这个时间的日志文件将被删除,小于等于0表示不清除
    const val storageDuration: Int = 1 * 24 * 7

    //日志文件的最大大小，单位字节，超过这个大小的后会轮转日志文件
    const val maxFileSize: Int = 1 * 1024 * 1024 * 5

    //日志文件的最小大小，单位字节，最小100KB
    const val minFileSize: Int = 1024 * 100

    //日志文件名字的前缀
    const val filePrefix: String = "log"


    //日志阻塞队列的容量
    const val logCapacity: Int = 1000

    const val minLogCapacity: Int = 100

    //主动触发日志写入的间隔时间，单位秒
    const val writeLogInterval: Long = 60

    const val minWriteLogInterval: Long = 10

    //写入前存放在内存中的日志条数
    const val memoryCacheSize: Int = 200

    const val minCacheSize: Int = 10
}