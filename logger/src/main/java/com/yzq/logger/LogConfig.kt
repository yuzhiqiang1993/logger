package com.yzq.logger

import com.yzq.logger.printer.IPrinter


/**
 * @description: Logger 配置
 * @author : yuzhiqiang
 */

class LogConfig private constructor() {
    //是否启用
    var enable = false

    //全局tag
    var tag = "Logger"

    //是否显示堆栈信息
    var showStackTrace = true

    //是否显示边框
    var showBorder = true

    //是否显示线程信息
    var showThreadInfo = true

    //是否显示可视化
    var showLogView = false

    //每行显示的字符数
    var lineLength = 4000

    //打印器列表
    var printers = arrayListOf<IPrinter>()


    class Builder {
        private val config = LogConfig()

        fun enable(enable: Boolean): Builder {
            config.enable = enable
            return this
        }

        fun globalTag(tag: String): Builder {
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

        fun showLogView(showLogView: Boolean): Builder {
            config.showLogView = showLogView
            return this
        }

        fun lineLength(lineLength: Int): Builder {
            config.lineLength = lineLength
            return this
        }

        fun addPrinter(printer: IPrinter): Builder {
            if (config.printers.contains(printer)) return this
            config.printers.add(printer)
            return this
        }


        fun build() = config
    }

    override fun toString(): String {
        return "LogConfig(enable=$enable, tag='$tag', showStackTrace=$showStackTrace, showBorder=$showBorder,showThreadInfo=$showThreadInfo, showLogView=$showLogView, lineLength=$lineLength)"
    }

}