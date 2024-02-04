package com.yzq.logger.core


/**
 * @description: 配置抽象类，用于配置打印器的配置信息，如是否启用，tag等信息
 * @author : yuzhiqiang
 */

abstract class AbsLogConfig {

    //是否启用
    open var enable = false

    //默认tag
    open var tag = "Logger"

    //是否显示堆栈信息
    var showStackTrace = true

    //是否显示线程信息
    var showThreadInfo = true


}