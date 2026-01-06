package com.yzq.logger.common

import android.graphics.Color
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


//获取堆栈信息时要忽略的类路径
private val ignoreClassNames = arrayOf(
    "com.yzq.logger."
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


internal fun Long.toFormatTimestamp(): String = runCatching {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    formatter.format(this)
}.getOrDefault(this.toString())

//换行符
internal val crlf = System.getProperty("line.separator") ?: "\n"

//空格
internal val space: String = "   "

//带分割线的空格
internal val spaceLine: String = " | "


/**
 * 格式化日志头部信息
 * @param tag String
 * @param traceInfo String?
 * @param logType LogType?
 * @param timeMillis Long?
 * @param threadName String?
 * @return String
 */
internal fun formatLogHeader(
    tag: String,
    traceInfo: String? = null,
    logType: LogType? = null,
    timeMillis: Long? = null,
    threadName: String? = null,

    ): String {
    val sb = StringBuilder().append(tag).append(spaceLine)

    if (timeMillis != null) {
        sb.append(timeMillis.toFormatTimestamp()).append(spaceLine)
    }
    if (logType != null) {
        sb.append("$logType").append(spaceLine)
    }
    if (threadName != null) {
        sb.append("$threadName").append(spaceLine)
    }
    if (traceInfo != null) {
        sb.append("$traceInfo").append(spaceLine)
    }

    //去掉最后一个spaceLine
    sb.delete(sb.length - spaceLine.length, sb.length)

    return sb.toString()
}

internal fun formatLogContent(contentStr: String, linePrefix: String = space): String {
    val sb = StringBuilder()
    contentStr.split(crlf).forEach {
        sb.append(linePrefix).appendLine(it)
    }
    return sb.toString()
}


internal fun getLogColor(logType: LogType): Int {
    return when (logType) {
        LogType.VERBOSE -> Color.parseColor("#444444") // 深灰色
        LogType.DEBUG -> Color.parseColor("#0000FF")   // 蓝色
        LogType.INFO -> Color.parseColor("#00AA00")    // 绿色
        LogType.WARN -> Color.parseColor("#FFA500")    // 金色
        LogType.ERROR -> Color.parseColor("#FF0000")   // 红色
        LogType.WTF -> Color.parseColor("#8B0000")     // 深红色
    }
}
