package com.yzq.logger.file

import com.yzq.logger.base.AbsPrinter
import com.yzq.logger.common.LogType
import com.yzq.logger.common.firstStackTraceInfo
import com.yzq.logger.data.LogItem
import java.lang.Thread.currentThread
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue


/**
 * @description: 文件打印器，用于将日志打印到文件中
 * @author : yuzhiqiang
 */
class FileLogPrinter private constructor(override val config: FileLogConfig) :
    AbsPrinter(config, FileLogFormatter.instance) {

    //存放日志的阻塞队列
    private val logBlockingQueue = LinkedBlockingQueue<LogItem>(1000)

    //日志写入线程
    private val executorService = Executors.newSingleThreadExecutor()


    companion object {

        @Volatile
        private var instance: FileLogPrinter? = null

        //日志文件管理器
        private var fileLogManager: FileLogManager? = null

        fun getInstance(config: FileLogConfig = FileLogConfig()): FileLogPrinter {
            return instance ?: synchronized(this) {
                instance ?: FileLogPrinter(config).also {
                    instance = it
                    fileLogManager = FileLogManager.getInstance(config, FileLogFormatter.instance)
                    fileLogManager?.clearExpiredLogFiles()
                    it.startWriteLog()
                }
            }

        }
    }


    override fun print(logType: LogType, tag: String?, vararg content: Any) {
        if (!config.enable) {
            return
        }
//        if (config.logFilePath.isEmpty() || config.logFilePath.isNotDir()) {
//            println("日志文件路径为空或者不是一个文件夹")
//            return
//        }

        if (logType.level >= config.minLevel.level) {
            //将日志加入到阻塞队列中，等待写入，如果队列满了，则丢弃，不会阻塞，不会抛出异常
            logBlockingQueue.offer(
                LogItem(
                    tag ?: config.tag,
                    logType,
                    System.currentTimeMillis(),
                    currentThread().name,
                    Throwable().firstStackTraceInfo(),
                    content = content
                )
            )
        }


    }

    private fun startWriteLog() {
        if (!config.enable) {
            return
        }
//        if (config.logFilePath.isEmpty() || config.logFilePath.isNotDir()) {
//            println("日志文件路径为空或者不是一个文件夹")
//            return
//        }
        //启动日志写入线程
        executorService.submit {
            while (currentThread().isInterrupted.not()) {
                runCatching {
                    val log = logBlockingQueue.take()
                    //写入日志到文件中
                    fileLogManager?.writeLogToFile(log)
                }.onFailure {
                    it.printStackTrace()
                }
            }

        }
    }


}




