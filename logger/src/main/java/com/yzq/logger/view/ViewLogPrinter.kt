package com.yzq.logger.view

import com.yzq.logger.common.LogType
import com.yzq.logger.core.AbsPrinter


/**
 * @description: 视图打印器
 * @author : yuzhiqiang
 */

class ViewLogPrinter private constructor(
    override val config: ViewLogConfig
) : AbsPrinter(config, ViewLogFormatter.getInstance(config)) {

    companion object {
        @Volatile
        private var instance: ViewLogPrinter? = null
        fun getInstance(config: ViewLogConfig = ViewLogConfig.Builder().build()): ViewLogPrinter {
            return instance ?: synchronized(this) {
                instance ?: ViewLogPrinter(config).also { instance = it }
            }
        }
    }


    override fun print(logType: LogType, tag: String?, vararg content: Any) {

        //todo 把日志数据存到内存中


    }


}