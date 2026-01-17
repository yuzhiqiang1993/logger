package com.yzq.logger.file

import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.logger.common.LogType
import com.yzq.logger.common.firstStackTraceInfo
import com.yzq.logger.core.AbsPrinter
import com.yzq.logger.data.LogItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


/**
 * @description: 文件日志打印器
 * @author : yuzhiqiang
 */
class FileLogPrinter private constructor() : AbsPrinter(), AppStateListener {

    // 日志流
    private var logFlow: MutableSharedFlow<LogItem>? = null

    private val printerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        AppManager.addAppStateListener(this)
    }

    companion object {

        @Volatile
        private var instance: FileLogPrinter? = null

        fun getInstance(config: FileLogConfig = FileLogConfig.Builder().build()): FileLogPrinter {
            return instance ?: synchronized(this) {
                instance ?: FileLogPrinter().also {
                    // 将配置信息应用到内部配置类
                    InternalFileLogConfig.apply(config)
                    it.logFlow = MutableSharedFlow(
                        replay = 0,
                        extraBufferCapacity = InternalFileLogConfig.logCapacity,
                        BufferOverflow.SUSPEND
                    )
                    instance = it

                    // 启动日志写入线程
                    it.startPrintService()

                }
            }

        }
    }


    override fun print(logType: LogType, tag: String, vararg content: Any) {
        if (!InternalFileLogConfig.enable || logFlow == null) {
            return
        }

        if (logType.level >= InternalFileLogConfig.minLevel.level) {
            // 直接在当前线程调用，避免创建协程的开销
            val logItem = LogItem(
                if (tag.isEmpty()) InternalFileLogConfig.tag else tag,
                logType,
                System.currentTimeMillis(),
                content = content,
            ).also {
                if (InternalFileLogConfig.showThreadInfo) {
                    it.threadName = Thread.currentThread().name
                }
                if (InternalFileLogConfig.showStackTrace) {
                    it.traceInfo = Throwable().firstStackTraceInfo()
                }
            }
            logFlow?.tryEmit(logItem)
        }
    }


    private fun startPrintService() {
        if (!InternalFileLogConfig.enable) {
            return
        }

        printerScope.launch {
            logFlow?.collect {
                FileLogWriter.addLog(it)
            }
        }

    }


    override fun onAppExit() {
        AppManager.removeAppStateListener(this)
    }


}
