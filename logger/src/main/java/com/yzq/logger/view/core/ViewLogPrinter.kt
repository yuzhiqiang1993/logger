package com.yzq.logger.view.core

import androidx.lifecycle.ViewModelProvider
import com.yzq.logger.common.LogType
import com.yzq.logger.core.AbsPrinter
import com.yzq.logger.view.log_view.ViewLogVMStoreOwner
import com.yzq.logger.view.log_view.ViewLogVm


/**
 * @description: 视图打印器
 * @author : yuzhiqiang
 */

class ViewLogPrinter private constructor() : AbsPrinter() {


    private val logVm =
        ViewModelProvider(ViewLogVMStoreOwner.instance).get(ViewLogVm::class.java)


    companion object {
        @Volatile
        private var instance: ViewLogPrinter? = null
        fun getInstance(config: ViewLogConfig = ViewLogConfig.Builder().build()): ViewLogPrinter {
            return instance ?: synchronized(this) {
                instance ?: ViewLogPrinter().also {
                    instance = it
                    InternalViewLogConfig.apply(config)
                }
            }
        }
    }


    override fun print(logType: LogType, tag: String?, vararg content: Any) {
        if (!InternalViewLogConfig.enable) return
        logVm.emitLog(logType, tag ?: InternalViewLogConfig.tag, *content)
    }


}