package com.yzq.logger.core


@Volatile
internal var loggerDebug = false

internal fun String.println() {
    if (loggerDebug) {
        println("$this,thread:${Thread.currentThread().name}")
    }
}