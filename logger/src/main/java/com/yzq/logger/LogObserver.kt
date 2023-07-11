package com.yzq.logger


/**
 * @description 日志打印拦截器, 可以用来拦截日志
 * @author  yuzhiqiang (zhiqiang.yu.xeon@gmail.com)
 */

interface LogObserver {
    fun log(info: LogInfo)
}