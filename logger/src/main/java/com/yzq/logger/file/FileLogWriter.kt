package com.yzq.logger.file

import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.AppStorage
import com.yzq.coroutine.interval.interval
import com.yzq.coroutine.safety_coroutine.getThreadInfo
import com.yzq.logger.core.ThreadPoolManager
import com.yzq.logger.core.invokeAllTask
import com.yzq.logger.data.LogItem
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale
import java.util.concurrent.Callable


/**
 * @description: 文件日志写入器
 * @author : yuzhiqiang
 */

internal class FileLogWriter private constructor(
    private val config: FileLogConfig
) : AppStateListener {


    init {
        AppManager.addAppStateListener(this)
    }

    //日志文件的存储目录,默认是/data/user/0/com.xxx.xxxx/files/.log/
    private var logFileDir = ""

//    //存放日志的缓冲map,key是要写入的文件，value是日志列表
//    private val logBufferMap = ConcurrentHashMap<File, MutableList<LogItem>>()

    //用于存放日志数据的线程安全的list
    private val logBufferList = Collections.synchronizedList(mutableListOf<LogItem>())

    //最后一次写入的时间
    private var lastWriteTime = System.currentTimeMillis()

    //用于执行定时任务的定时器
    private val interval = interval(config.writeLogInterval, initialDelay = config.writeLogInterval)


    //存放日志文件的列表
    private var existLogFileList = listLogDirFiles()

    //执行写操作的线程池
    private val writeLogExecutor = ThreadPoolManager.instance.ioThreadPoolExecutor


    //执行写如操作的taskList
    private val writeLogTaskList = mutableListOf<Callable<Boolean>>()

    companion object {

        @Volatile
        var instance: FileLogWriter? = null

        fun getInstance(
            config: FileLogConfig = FileLogConfig()
        ): FileLogWriter {
            return instance ?: synchronized(this) {
                instance ?: FileLogWriter(config).also {
                    instance = it
                    it.init()
                }
            }

        }

    }

    private fun init() {
        if (config.maxCacheSize <= FileLogConstant.maxCacheSize) {
            config.maxCacheSize = FileLogConstant.maxCacheSize
        }

        if (config.writeLogInterval <= FileLogConstant.writeLogInterval) {
            config.writeLogInterval = FileLogConstant.writeLogInterval
        }

        if (config.dirName.isEmpty()) {
            config.dirName = ".log"
        }
        logFileDir =
            "${AppStorage.Internal.filesPath}${File.separator}${config.dirName}${File.separator}"

        //目录如果不存在，先创建
        val logFileDir = File(logFileDir)
        if (!logFileDir.exists()) {
            logFileDir.mkdirs()
        }

        println("日志目录:$logFileDir,准备完毕")
        clearExpiredLogFiles()

        //启动一个定时任务，用于定期写入日志,避免出现日志条数不满足条件，导致日志不写入的情况
        interval.subscribe {
            println("定时任务开始执行:${config.writeLogInterval},次数:${it}")
            flushLog(true)
        }.start()

    }


    @Synchronized
    fun writeLog(log: LogItem) {
        logBufferList.add(log)
        flushLog(false)
    }

    private fun flushLog(forceFlush: Boolean = false) {
        println("flushLog forceFlush:$forceFlush,threadInfo:${getThreadInfo()}")
        if (logBufferList.size <= 0) return
        if (logBufferList.size >= config.maxCacheSize || forceFlush) {
            println("满足写入条件，开始写入日志")
            val logBufferMap = logBufferList.groupBy {
                getOperateFile(it)
            }.filter {
                it.key?.exists() == true
            }


            logBufferMap.forEach { (file, logList) ->
                file?.run {
                    writeLogTaskList.add(doFileWrite(file, logList))
                }
            }

            println("执行写入任务，任务数量:${writeLogTaskList.size}")
            writeLogExecutor.invokeAllTask(writeLogTaskList)
            println("写入任务执行完毕")

            writeLogTaskList.clear()
            logBufferList.clear()
            lastWriteTime = System.currentTimeMillis()

        } else {
            println("不满足写入条件")
        }


    }

    private fun doFileWrite(file: File, logList: List<LogItem>): Callable<Boolean> {
        return Callable {
            synchronized(file) {
                runCatching {
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    println("写文件的线程信息：${getThreadInfo()}")
                    val sb = StringBuilder()
                    logList.forEach { logItem ->
                        sb.append(
                            FileLogFormatter.instance.formatToStr(logItem)
                        )
                    }
                    //将内容追加到文件中
                    BufferedWriter(FileWriter(file, true)).use { bw ->
                        bw.append(sb.toString())
                        bw.flush()
                    }
                    println("${file.name} 日志写入完成，条数：${logList.size}")
                    sb.clear()
                }
                true
            }
        }
    }

    /**
     * 清除过期的日志文件
     */

    private fun clearExpiredLogFiles() {
        println("clearExpiredLogFiles storageDuration:${config.storageDuration}")

        if (config.storageDuration <= 0) {
            return
        }
        //计算过期时间点
        val expiredTime = System.currentTimeMillis() - config.storageDuration * 60 * 60 * 1000

        existLogFileList.filter {
            it.isFile && it.lastModified() < expiredTime
        }.forEach {
            println("删除过期日志文件:${it.name}")
            synchronized(it) {
                it.delete()
            }
        }

        //重新获取一下文件列表
        updateExistLogFileList()

    }


    /**
     * 获取日志文件夹下的所有日志文件
     * @return List<File>
     */
    private fun listLogDirFiles(): List<File> {
        val logFileDir = File(logFileDir)
        if (!logFileDir.exists()) {
            return emptyList()
        }
        return logFileDir.listFiles()?.filter {
            it.isFile
        } ?: emptyList()
    }


    /**
     * 获取要操作的文件
     * @param filePrefix String
     * @return File?
     */
    @Synchronized
    private fun getOperateFile(log: LogItem): File? = runCatching {
        //当前年-月-日-时
        val logTime = SimpleDateFormat(
            "yyyy-MM-dd-HH", Locale.getDefault()
        ).format(log.timeMillis)

        //先确定文件名，规则为：文件名前缀-yyyy-MM-dd-HH-index.txt，例如：2024-01-14-14-1.txt
        val filePrefix = if (config.filePrefix.isNotEmpty()) {
            "${config.filePrefix}-${logTime}"
        } else {
            logTime
        }

        //获取以当前时间点开头的文件列表
        val existTimeFileList =
            existLogFileList.filter { it.name.startsWith(filePrefix) && it.name.endsWith(".txt") }

        var fileIndex = 1

        //如果不存在以当前时间点开头的文件，则创建一个
        if (existTimeFileList.isEmpty()) {
            println("不存在当前时间点的文件，新建")
            val newFile = File(logFileDir, "${filePrefix}-${fileIndex}.txt").also {
                it.createNewFile()
            }
            updateExistLogFileList()
            return@runCatching newFile
        }

        //获取当前时间点文件名的最大index,实际上直接获取列表数量即可
        fileIndex = existTimeFileList.size
        val operateFile = File(logFileDir, "${filePrefix}-$fileIndex.txt")

        //这里判断下如果不存在要创建一下，避免不可预知的错误
        if (!operateFile.exists()) {
            operateFile.createNewFile()
            updateExistLogFileList()
        }
        //如果文件大小未超过限制，则直接返回
        if (operateFile.length() <= config.maxFileSize) {
            return operateFile
        }

        //创建一个新的文件
        println("创建新文件")
        fileIndex = existTimeFileList.size + 1
        val newFile = File(logFileDir, "${filePrefix}-${fileIndex}.txt").also {
            it.createNewFile()
        }
        updateExistLogFileList()

        return@runCatching newFile
    }.getOrDefault(null)

    private fun updateExistLogFileList() {
        existLogFileList = listLogDirFiles()
    }


    override fun onAppExit() {
        super.onAppExit()
        flushLog(true)
//        ThreadPoolManager.instance.ioThreadPoolExecutor.shutdown()

        AppManager.removeAppStateListener(this)

    }

}