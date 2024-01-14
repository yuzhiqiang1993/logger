package com.yzq.logger_demo.customer

import com.yzq.logger.base.AbsPrinter
import com.yzq.logger.common.LogType


/**
 * @description: 自定义的打印器，例如上传到服务器等等操作
 * @author : yuzhiqiang
 */
class CustomerPrinter : AbsPrinter(CustomerConfig().apply {
    enable = true
    tag = "CustomerPrinter"
}, CustomerFormater()) {
    override fun print(logType: LogType, tag: String?, vararg content: Any) {
        val finalTag = tag ?: config.tag

        val formatToStr = formatter.formatToStr(logType, finalTag, *content)
        println("自定义的打印器打印的内容：${formatToStr}")
    }
}