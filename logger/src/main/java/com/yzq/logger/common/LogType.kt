package com.yzq.logger.common


/**
 * @description: 日志级别
 * @author : yuzhiqiang
 */

enum class LogType(val level: Int) {

    //最低级别的日志，用于输出大量的调试信息。 通常包含程序运行的详细信息，如变量值、状态信息等。
    VERBOSE(1),

    // 用于输出调试信息，适用于记录程序运行状态、执行流程等调试信息。
    DEBUG(2),

    // 用于输出一般提示信息，用于记录程序的关键运行信息和有用的状态数据，但不是专为调试目的。
    INFO(3),

    // 用于输出警告信息，通常表明可能会影响应用程序运行的问题。
    WARN(4),

    // 用于输出错误信息，用于记录错误和异常情况，表明应用中发生了严重的问题，需要开发者关注和处理。
    ERROR(5),

    // WTF（What a Terrible Failure），用于输出非常严重的错误信息，通常会导致应用中止运行。
    WTF(6)
}
