package com.yzq.logger.file

import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.coroutine.thread_pool.ThreadPoolManager
import com.yzq.logger.common.LogType
import com.yzq.logger.common.firstStackTraceInfo
import com.yzq.logger.core.AbsPrinter
import com.yzq.logger.data.LogItem
import java.lang.Thread.currentThread
import java.util.concurrent.LinkedBlockingQueue


/**
 * @description: 文件日志打印器
 * @author : yuzhiqiang
 */
class FileLogPrinter private constructor(override val config: FileLogConfig) :
    AbsPrinter(config, FileLogFormatter.instance), AppStateListener {


    //存放日志的阻塞队列
    private var logBlockingQueue: LinkedBlockingQueue<LogItem>? = null

    //只有一个线程的线程池
    private val singleThreadPoolExecutor by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ThreadPoolManager.instance.fixedThreadPoolExecutor(1)
    }


    init {
        AppManager.addAppStateListener(this)
    }


    companion object {

        @Volatile
        private var instance: FileLogPrinter? = null

        //文件日志写入器
        @Volatile
        private var fileLogWriter: FileLogWriter? = null

        fun getInstance(config: FileLogConfig = FileLogConfig()): FileLogPrinter {
            return instance ?: synchronized(this) {
                instance ?: FileLogPrinter(config).also {
                    if (config.logQueueCapacity <= FileLogConstant.logQueueCapacity) {
                        config.logQueueCapacity = FileLogConstant.logQueueCapacity
                    }
                    //初始化阻塞队列
                    it.logBlockingQueue = LinkedBlockingQueue(config.logQueueCapacity)

                    //写入日志的间隔时间不能小于默认值，但是也不能大于
                    if (config.writeLogInterval < FileLogConstant.writeLogInterval) {
                        config.writeLogInterval = FileLogConstant.writeLogInterval
                    }
                    instance = it
                    fileLogWriter = FileLogWriter.getInstance(config)

                    //启动日志写入线程
                    it.startPrintService()

                }
            }

        }
    }


    override fun print(logType: LogType, tag: String?, vararg content: Any) {
        if (!config.enable || logBlockingQueue == null) {
            return
        }

        if (logType.level >= config.minLevel.level) {
            //将日志加入到阻塞队列中，等待写入，如果队列满了，则丢弃，不会阻塞，不会抛出异常
            logBlockingQueue?.offer(LogItem(
                tag ?: config.tag, logType, System.currentTimeMillis(), content = content,
            ).also {
                if (config.showThreadInfo) {
                    it.threadName = currentThread().name
                }
                if (config.showStackTrace) {
                    it.traceInfo = Throwable().firstStackTraceInfo()
                }
            })
        }
    }


    private fun startPrintService() {
        if (!config.enable) {
            return
        }
        //启动日志写入线程
        singleThreadPoolExecutor.execute {
            while (currentThread().isInterrupted.not()) {
                runCatching {
                    logBlockingQueue?.take()?.let {
                        //写入日志到文件中
                        fileLogWriter?.writeLog(it)
                    }
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }


    override fun onAppExit() {
        singleThreadPoolExecutor.shutdown()
        AppManager.removeAppStateListener(this)

    }


}




