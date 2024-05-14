package com.yzq.logger.view.core

import androidx.lifecycle.ViewModelProvider
import com.yzq.logger.common.LogType
import com.yzq.logger.core.AbsPrinter
import com.yzq.logger.data.ViewLogItem
import com.yzq.logger.view.log_view.ViewLogVMStoreOwner
import com.yzq.logger.view.log_view.ViewLogVm


/**
 * @description: 视图打印器
 * @author : yuzhiqiang
 */

class ViewLogPrinter private constructor(
    override val config: ViewLogConfig
) : AbsPrinter(config, ViewLogFormatter.getInstance(config)) {


    private val logVm =
        ViewModelProvider(ViewLogVMStoreOwner.instance).get(ViewLogVm::class.java)


    companion object {
        @Volatile
        private var instance: ViewLogPrinter? = null
        fun getInstance(config: ViewLogConfig = ViewLogConfig.Builder().build()): ViewLogPrinter {
            return instance ?: synchronized(this) {
                instance ?: ViewLogPrinter(config).also {
                    instance = it
                }
            }
        }
    }


    override fun print(logType: LogType, tag: String?, vararg content: Any) {
        if (!config.enable) return
        val logStr =
            ViewLogFormatter.getInstance(config).formatToStr(logType, tag ?: config.tag, *content)
        logVm.log(ViewLogItem(logType = logType, content = logStr))
    }


}