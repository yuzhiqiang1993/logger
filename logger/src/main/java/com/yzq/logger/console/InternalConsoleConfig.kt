package com.yzq.logger.console

import com.yzq.logger.core.AbsLogConfig

/**
 * @description: 控制台打印的配置，内部使用，不对外暴露
 * @author: yuzhiqiang
 */
internal object InternalConsoleConfig : AbsLogConfig() {
    // 是否显示边框
    var showBorder: Boolean = true

    // 每行显示的字符数,最小500，最大4000
    var lineLength: Int = 4000


    fun apply(config: ConsoleLogConfig) {
        enable = config.enable
        tag = config.tag
        showStackTrace = config.showStackTrace
        showThreadInfo = config.showThreadInfo
        lineLength = config.lineLength
        showBorder = config.showBorder

    }

}
