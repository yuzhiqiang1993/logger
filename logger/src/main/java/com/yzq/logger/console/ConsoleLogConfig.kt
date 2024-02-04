package com.yzq.logger.console

import com.yzq.logger.core.AbsLogConfig


/**
 * @description: 控制台打印器配置类
 * @author : yuzhiqiang
 */

class ConsoleLogConfig : AbsLogConfig() {


    //是否显示边框
    var showBorder = true

    //每行显示的字符数,最小500，最大4000
    var lineLength = 4000


    fun enable(enable: Boolean): ConsoleLogConfig {
        this.enable = enable
        return this
    }

    fun showBorder(showBorder: Boolean): ConsoleLogConfig {
        this.showBorder = showBorder
        return this
    }

    fun lineLength(lineLength: Int): ConsoleLogConfig {
        this.lineLength = lineLength
        return this
    }

    fun showStackTrace(showStackTrace: Boolean): ConsoleLogConfig {
        this.showStackTrace = showStackTrace
        return this
    }

    fun showThreadInfo(showThreadInfo: Boolean): ConsoleLogConfig {
        this.showThreadInfo = showThreadInfo
        return this
    }


}