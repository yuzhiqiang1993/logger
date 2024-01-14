package com.yzq.logger.console

import com.yzq.logger.base.AbsLogConfig


/**
 * @description: 控制台打印器配置类
 * @author : yuzhiqiang
 */

class ConsoleLogConfig private constructor() : AbsLogConfig() {


    //是否显示边框
    var showBorder = true

    //每行显示的字符数,最小500，最大4000
    var lineLength = 4000


    class Builder {
        private val config = ConsoleLogConfig()

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


        fun showTimestamp(showTimestamp: Boolean): Builder {
            config.showTimestamp = showTimestamp
            return this
        }

        fun build() = config
    }


}