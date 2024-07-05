package com.yzq.logger.view.log_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yzq.coroutine.safety_coroutine.launchSafety
import com.yzq.logger.common.LogType
import com.yzq.logger.data.ViewLogItem
import com.yzq.logger.view.core.InternalViewLogConfig
import com.yzq.logger.view.core.ViewLogFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow


/**
 * @description: 视图日志ViewModel，用于日志流的处理，日志流的发射和接收
 * @author : yuzhiqiang
 */

internal class ViewLogVm private constructor() : ViewModel() {


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

    var logsSharedFlow: MutableSharedFlow<ViewLogItem>? = null

    fun emitLog(logType: LogType, tag: String, vararg content: Any) {
        if (logsSharedFlow == null) {
            logsSharedFlow = MutableSharedFlow(
                replay = InternalViewLogConfig.cacheSize,
                extraBufferCapacity = 0,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }

        viewModelScope.launchSafety(Dispatchers.IO) {
            val logStr = ViewLogFormatter.formatToStr(logType, tag, *content)
            logsSharedFlow?.tryEmit(ViewLogItem(logType = logType, content = logStr))
        }
    }


}


