package com.yzq.logger.core


private val TAG = "LoggerPrintln"

@Volatile
var loggerDebug = false

fun String.println() {
    if (loggerDebug) {
        println("$TAG: $this")
    }
}
