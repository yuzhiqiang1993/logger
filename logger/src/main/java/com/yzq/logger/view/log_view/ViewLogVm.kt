package com.yzq.logger.view.log_view

import androidx.lifecycle.ViewModel
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

    val logsSharedFlow: MutableSharedFlow<ViewLogItem> = MutableSharedFlow(
        replay = InternalViewLogConfig.cacheSize,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun emitLog(logType: LogType, tag: String, vararg content: Any) {

        viewModelScope.launchSafety(Dispatchers.IO) {
            val logStr = ViewLogFormatter.formatToStr(logType, tag, *content)
            logsSharedFlow.tryEmit(ViewLogItem(logType = logType, content = logStr))
        }
    }


}


