package com.yzq.logger.formater


/**
 * @description: 格式化接口
 * @author : yuzhiqiang
 */

interface IFormatter {
    fun formatToStr(tag: String, vararg content: Any): String
}