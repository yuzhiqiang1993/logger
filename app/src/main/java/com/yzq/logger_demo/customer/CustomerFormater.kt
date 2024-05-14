package com.yzq.logger_demo.customer

import com.yzq.logger.common.LogType
import com.yzq.logger.core.ILogFormatter


/**
 * @description: 自定义的内容格式化器
 * @author : yuzhiqiang
 */

object CustomerFormater : ILogFormatter {


    override fun formatToStr(logType: LogType, tag: String, vararg content: Any): String {
        return "CustomerFormater---》tag:$tag,logType:${logType}, content:${content.contentToString()}"
    }
}