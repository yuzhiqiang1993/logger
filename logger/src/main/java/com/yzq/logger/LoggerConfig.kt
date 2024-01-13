package com.yzq.logger


/**
 * @description: Logger 配置
 * @author : yuzhiqiang
 */

class LoggerConfig private constructor() {
    //是否启用
    var enable = false

    //默认tag
    var tag = "Logger"

    //是否显示堆栈信息
    var showStackTrace = true

    //是否显示边框
    var showBorder = true

    //是否显示线程信息
    var showThreadInfo = true


    //每行显示的字符数
    var lineLength = 4000


    class Builder {
        private val config = LoggerConfig()

        fun enable(enable: Boolean): Builder {
            config.enable = enable
            return this
        }

        fun tag(tag: String): Builder {
            config.tag = tag
            return this
        }

        fun showStackTrace(showStackTrace: Boolean): Builder {
            config.showStackTrace = showStackTrace
            return this
        }

        fun showBorder(showBorder: Boolean): Builder {
            config.showBorder = showBorder
            return this
        }


        fun showThreadInfo(showThreadInfo: Boolean): Builder {
            config.showThreadInfo = showThreadInfo
            return this
        }

        fun lineLength(lineLength: Int): Builder {
            config.lineLength = lineLength
            return this
        }


        fun build() = config
    }


}