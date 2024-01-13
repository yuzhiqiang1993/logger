package com.yzq.logger.formater

import com.yzq.logger.LoggerConfig


/**
 * @description: 格式化接口
 * @author : yuzhiqiang
 */

interface IFormatter {
    fun formatToStr(config: LoggerConfig, tag: String, vararg content: Any): String
}