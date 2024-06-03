package com.yzq.logger.console

import com.yzq.logger.core.AbsLogConfig

/**
 * @description: 对外暴露的控制台打印的配置类 Builder 模式
 * @author: yuzhiqiang
 */
class ConsoleLogConfig private constructor(builder: Builder) : AbsLogConfig(
    builder.enable, builder.tag, builder.showStackTrace, builder.showThreadInfo
) {

    val showBorder = builder.showBorder
    val lineLength = builder.lineLength


    class Builder {

        var enable: Boolean = true
            private set

        var tag: String = "ConsoleLog"
            private set

        var showStackTrace: Boolean = true
            private set

        var showThreadInfo: Boolean = true
            private set

        // 每行显示的字符数,最小500，最大4000
        var lineLength: Int = 4000
            private set

        var showBorder: Boolean = true
            private set

        fun enable(enable: Boolean): Builder {
            this.enable = enable
            return this
        }

        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun showStackTrace(showStackTrace: Boolean): Builder {
            this.showStackTrace = showStackTrace
            return this
        }

        fun showThreadInfo(showThreadInfo: Boolean): Builder {
            this.showThreadInfo = showThreadInfo
            return this
        }


        fun lineLength(lineLength: Int): Builder {
            // 检查 lineLength 是否在有效范围内
            if (lineLength < 500 || lineLength > 4000) {
                throw IllegalArgumentException("lineLength must be >= 500 and <= 4000")
            }
            this.lineLength = lineLength
            return this
        }

        fun showBorder(showBorder: Boolean): Builder {
            this.showBorder = showBorder
            return this
        }

        fun build(): ConsoleLogConfig {
            return ConsoleLogConfig(this)
        }


    }
}
