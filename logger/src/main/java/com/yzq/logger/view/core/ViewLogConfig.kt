package com.yzq.logger.view.core

import com.yzq.logger.core.AbsLogConfig

/**
 * @description: 视图打印器配置类 Builder 模式
 * @author: yuzhiqiang
 */
class ViewLogConfig private constructor(builder: Builder) : AbsLogConfig(
    builder.enable, builder.tag, builder.showStackTrace, builder.showThreadInfo
) {

    val lineLength = builder.lineLength
    val cacheSize = builder.cacheSize


    class Builder {

        var enable: Boolean = true
            private set

        var tag: String = "ViewLog"
            private set

        var showStackTrace: Boolean = true
            private set

        var showThreadInfo: Boolean = true
            private set

        // 每行显示的字符数,最小500，最大4000
        var lineLength: Int = 4000
            private set

        var cacheSize: Int = 1000
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

        fun cacheSize(cacheSize: Int): Builder {
            if (cacheSize < 0) {
                throw IllegalArgumentException("cacheSize must be >= 0")
            }
            this.cacheSize = cacheSize
            return this
        }

        fun build(): ViewLogConfig {
            return ViewLogConfig(this)
        }


    }
}
