package com.yzq.logger.view.core

import com.yzq.logger.core.AbsLogConfig

/**
 * @description: 视图打印器配置类 Builder 模式
 * @author: yuzhiqiang
 */
internal object InternalViewLogConfig : AbsLogConfig() {

    var lineLength = 500
    var cacheSize = 1000

    fun apply(config: ViewLogConfig) {
        enable = config.enable
        tag = config.tag
        showStackTrace = config.showStackTrace
        showThreadInfo = config.showThreadInfo
        lineLength = config.lineLength
        cacheSize = config.cacheSize
    }


}
