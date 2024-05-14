package com.yzq.logger.view.log_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yzq.coroutine.safety_coroutine.launchSafety
import com.yzq.logger.data.ViewLogItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow


/**
 * @description: 视图日志ViewModel，用于日志流的处理，日志流的发射和接收
 * @author : yuzhiqiang
 */

class ViewLogVm private constructor() : ViewModel() {


    //原始数据的日志流，充当阻塞队列的作用，最多保存一定的数据量,数据满时会丢弃旧数据
    val logsSharedFlow: MutableSharedFlow<ViewLogItem> = MutableSharedFlow(
        replay = 100,//缓存大小
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun log(log: ViewLogItem) {
        viewModelScope.launchSafety(Dispatchers.IO) {
            logsSharedFlow.tryEmit(log)
        }
    }


}


