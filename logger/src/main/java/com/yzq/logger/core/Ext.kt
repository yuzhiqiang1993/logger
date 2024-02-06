package com.yzq.logger.core

import java.util.concurrent.locks.ReentrantLock


private val TAG = "LoggerPrintln"

@Volatile
var loggerDebug = false

fun String.println() {
    if (loggerDebug) {
        println("$TAG: $this,thread:${Thread.currentThread().name}")
    }
}


internal fun ReentrantLock.info(): String {
    return """
        是否公平锁：${this.isFair}
        是否被锁住：${this.isLocked}
        重入次数：${this.holdCount}
        是否有线程在等待获取锁：${this.hasQueuedThreads()}
        等待获取锁的线程数：${this.queueLength}
    
        """.trimIndent()


}