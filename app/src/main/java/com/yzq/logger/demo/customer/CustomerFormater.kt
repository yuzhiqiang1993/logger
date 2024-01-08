package com.yzq.logger.demo.customer

import com.yzq.logger.formater.IFormatter


/**
 * @description: 自定义的内容格式化器
 * @author : yuzhiqiang
 */

object CustomerFormater : IFormatter {

    override fun formatToStr(tag: String, vararg content: Any): String {


        return "CustomerFormater---》tag:$tag, content:${content.contentToString()}"
    }
}