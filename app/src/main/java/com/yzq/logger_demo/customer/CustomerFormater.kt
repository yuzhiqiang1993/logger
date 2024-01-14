package com.yzq.logger_demo.customer

import com.yzq.logger.base.ILogFormatter
import com.yzq.logger.common.LogType


/**
 * @description: 自定义的内容格式化器
 * @author : yuzhiqiang
 */

class CustomerFormater : ILogFormatter {


    override fun formatToStr(logType: LogType, tag: String, vararg content: Any): String {
        return "CustomerFormater---》tag:$tag,logType:${logType}, content:${content.contentToString()}"
    }
}