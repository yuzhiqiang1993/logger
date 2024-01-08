package com.yzq.logger.demo.customer

import com.yzq.logger.LogType
import com.yzq.logger.printer.IPrinter


/**
 * @description: 自定义的打印器，例如上传到服务器等等操作
 * @author : yuzhiqiang
 */
class CustomerPrinter : IPrinter {
    override fun print(logType: LogType, tag: String, vararg content: Any) {

        val formatToStr = CustomerFormater.formatToStr(tag, *content)
        println("自定义的打印器打印的内容：${formatToStr}")
    }
}