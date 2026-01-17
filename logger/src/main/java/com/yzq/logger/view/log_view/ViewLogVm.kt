package com.yzq.logger.view.log_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yzq.logger.common.LogType
import com.yzq.logger.data.ViewLogItem
import com.yzq.logger.view.core.InternalViewLogConfig
import com.yzq.logger.view.core.ViewLogFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


/**
 * @description: 视图日志ViewModel，用于日志流的处理，日志流的发射和接收
 * @author : yuzhiqiang
 */

@Suppress("UNCHECKED_CAST")
internal class ViewLogVm private constructor() : ViewModel() {

    // 用于接收原始日志的 Channel
    private val logInputChannel = Channel<RawLogInput>(
        capacity = InternalViewLogConfig.cacheSize,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    companion object {

        /**
         * 提供ViewModelProvider.Factory, ViewLogVm构造函数私有化了。
         * 使用示例：ViewModelProvider(ViewLogVMStoreOwner.instance,ViewLogVm.provideFactory()).get(ViewLogVm::class.java)。
         * 避免反射导致异常
         * @return ViewModelProvider.Factory
         */
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ViewLogVm::class.java)) {
                    return ViewLogVm() as T
                }
                return super.create(modelClass)
            }
        }
    }

    val logsSharedFlow = MutableSharedFlow<ViewLogItem>(
        replay = InternalViewLogConfig.cacheSize,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        // 启动协程处理所有日志格式化，避免频繁创建协程
        viewModelScope.launch(Dispatchers.IO) {
            logInputChannel.receiveAsFlow().collect { input ->
                val logInfo = ViewLogFormatter.parseLogInfo(input.logType, input.tag, *input.content)
                logsSharedFlow.tryEmit(
                    ViewLogItem(
                        logType = input.logType,
                        tag = logInfo.tag,
                        content = logInfo.content,
                        timestamp = logInfo.timestamp,
                        threadName = logInfo.threadName,
                        stackTrace = logInfo.traceInfo
                    )
                )
            }
        }
    }

    fun emitLog(logType: LogType, tag: String, vararg content: Any) {
        // 直接发送到 Channel，由后台协程统一处理
        logInputChannel.trySend(RawLogInput(logType, tag, content))
    }

    /**
     * 原始日志输入数据类
     */
    private data class RawLogInput(
        val logType: LogType,
        val tag: String,
        val content: Array<out Any>
    )
}


