package com.yzq.logger.view

import com.yzq.logger.core.AbsLogConfig

/**
 * @description: 视图打印器配置类 Builder 模式
 * @author: yuzhiqiang
 */
class ViewLogConfig private constructor(builder: Builder) : AbsLogConfig(
    builder.enable, builder.tag, builder.showStackTrace, builder.showThreadInfo
) {

    // 是否显示边框
    val showBorder: Boolean = builder.showBorder


    // 每行显示的字符数,最小500，最大4000
    val lineLength: Int = builder.lineLength

    //是否折叠显示
    val isFold: Boolean = builder.isFold


    class Builder {

        var enable: Boolean = true
            private set

        var tag: String = "ViewLog"
            private set

        var showStackTrace: Boolean = true
            private set

        var showThreadInfo: Boolean = true
            private set

        var showBorder: Boolean = false
            private set

        // 每行显示的字符数,最小500，最大4000
        var lineLength: Int = 4000
            private set

        var isFold: Boolean = true
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

        fun showBorder(showBorder: Boolean): Builder {
            this.showBorder = showBorder
            return this
        }

        fun isFold(isFold: Boolean): Builder {
            this.isFold = isFold
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

        fun build(): ViewLogConfig {
            return ViewLogConfig(this)
        }
    }
}
