package com.yzq.logger.printer

import com.yzq.logger.LogType


/**
 * @description: 文件打印器，用于将日志打印到文件中
 * @author : yuzhiqiang
 */

class FileLogPrinter private constructor() : AbsPrinter() {

    private var filePath: String = ""

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FileLogPrinter()
        }
    }


    override fun print(logType: LogType, tag: String?, vararg content: Any) {

        if (!loggerConfig.enable || filePath.isEmpty()) {
            return
        }

    }


    /**
     * 设置文件路径
     * @param filePath String
     */
    fun setFilePath(filePath: String) {
        this.filePath = filePath
    }
}