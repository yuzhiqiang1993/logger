package com.yzq.logger.ext

import android.util.Log
import com.yzq.logger.LogType


//获取堆栈信息时要忽略的类路径
private val ignoreClassNames = arrayOf(
    "com.yzq.logger.formater.ContentFormatter",
    "com.yzq.logger.ext",
    "com.yzq.logger.printer",
    "com.yzq.logger.Logger",

    )

/**
 * 获取第一个有用的堆栈信息
 * @receiver Throwable
 * @return String
 */
internal fun Throwable.firstStackTraceInfo(): String {
    var traceInfo = ""
    /*找出第一个符合条件的堆栈信息*/
    this.stackTrace.firstOrNull {
//        println("it.className = ${it.className}")
        ignoreClassNames.none { ignoreClassName ->
            it.className.startsWith(ignoreClassName)
        }
    }?.run {
        traceInfo = "${className}.$methodName($fileName:$lineNumber)"
    }

    return traceInfo
}


internal fun doLog(logType: LogType, tag: String, msg: String) {
    when (logType) {
        LogType.VERBOSE -> Log.v(tag, msg)
        LogType.DEBUG -> Log.d(tag, msg)
        LogType.INFO -> Log.i(tag, msg)
        LogType.WARN -> Log.w(tag, msg)
        LogType.ERROR -> Log.e(tag, msg)
        LogType.WTF -> Log.wtf(tag, msg)
    }
}