package com.yzq.logger.file

import com.yzq.application.AppManager
import com.yzq.application.AppStateListener
import com.yzq.application.AppStorage
import com.yzq.coroutine.interval.interval
import com.yzq.coroutine.thread_pool.ThreadPoolManager
import com.yzq.logger.core.println
import com.yzq.logger.data.LogItem
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock


/**
 * @description: 文件日志写入器
 * @author : yuzhiqiang
 */

internal object FileLogWriter : AppStateListener {


    // 日志文件的存储目录,默认是/data/user/0/com.xxx.xxxx/files/.log/
    private var logFileDir = ""


    // 用于存放阻塞队列过来的日志
    private val logBufferList = mutableListOf<LogItem>()


    // 最后一次写入的时间
    private var lastWriteTime = System.currentTimeMillis()

    // 用于执行定时任务的定时器
    private val interval = interval(
        InternalFileLogConfig.writeLogInterval,
        initialDelay = InternalFileLogConfig.writeLogInterval
    )

    // 存放日志文件的列表
    private var existLogFileList = listLogDirFiles()

    // 日志数据的锁
    private var logDataLock = ReentrantLock(true)

    // 写日志的锁
    private var writeLogLock = ReentrantLock(true)

    // 更新文件列表的锁
    private val updateFileLock = ReentrantLock(true)

    // 使用 ThreadLocal 缓存 SimpleDateFormat，避免频繁创建
    private val fileNameFormatter = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat("yyyy-MM-dd-HH", Locale.getDefault())
        }
    }

    // 文件查找缓存：key = filePrefix, value = 当前使用的文件
    // 每次批量写入开始时清空，避免过期问题
    private val fileCache = mutableMapOf<String, File>()


    init {

        logFileDir =
            "${AppStorage.Internal.filesPath}${File.separator}${InternalFileLogConfig.dirName}${File.separator}"

        //目录如果不存在，先创建
        val logFileDir = File(logFileDir)
        if (!logFileDir.exists()) {
            logFileDir.mkdirs()
        }

//        println("日志目录:$logFileDir,准备完毕")
        clearExpiredLogFiles()

        //启动一个定时任务，用于定期写入日志,避免出现日志条数不满足条件，导致日志不写入的情况
        interval.subscribe {
            "定时任务开始执行:${InternalFileLogConfig.writeLogInterval},次数:${it}".println()
            writeLog(true)
        }.start()

        AppManager.addAppStateListener(this)
    }


    /**
     * 需要保证添加log的原子性
     * @param log LogItem?
     * @param forceFlush Boolean
     */
    fun addLog(log: LogItem) {

        lockBlock(logDataLock) {
            logBufferList.add(log)
        }

        //放在这里是为了避免写入文件的耗时操作影响到添加log的速度
        writeLog(false)
    }

    private fun writeLog(forceWrite: Boolean) {

        if (!forceWrite && logBufferList.size < InternalFileLogConfig.memoryCacheSize) {
            "非强制写入并且日志数量不足${InternalFileLogConfig.memoryCacheSize}条，不写入".println()
            return
        }

        println("满足写入条件，日志总条数：${logBufferList.size},是否强制写入：$forceWrite")

        //准备数据
        lockBlock(logDataLock) {

            "logBufferList数量，${logBufferList.size}".println()
            if (logBufferList.isEmpty()) {
                return@lockBlock
            }
            //把logBufferList的数据存到一个list中
            val tempLogList = mutableListOf<LogItem>()
            tempLogList.addAll(logBufferList)
            "tmpLogList数量：${tempLogList.size}".println()
            logBufferList.clear()
            "清空 logBufferList,${logBufferList.size}".println()
            //异步执行写入文件的操作
            flushLog(tempLogList)
        }


    }

    private fun flushLog(tempLogList: MutableList<LogItem>) {
        if (tempLogList.isEmpty()) {
            "满足写入条件，但是日志数量为0，不写入".println()
            return
        }
        ThreadPoolManager.instance.ioThreadPoolExecutor.execute {
            lockBlock(writeLogLock) {
                "开始写入日志，日志总条数：${tempLogList.size}".println()
                
                // 清空文件缓存，确保每次批量写入都重新检查文件
                fileCache.clear()
                
                // 先按时间前缀分组，减少 getOperateFile 的调用和文件列表遍历次数
                val logsByPrefix = tempLogList.groupBy { getFilePrefix(it) }
                
                // 再为每个前缀分组获取目标文件
                val logBufferMap = mutableMapOf<File, MutableList<LogItem>>()
                logsByPrefix.forEach { (prefix, logs) ->
                    val file = getOperateFileByPrefix(prefix, logs.first())
                    logBufferMap.getOrPut(file) { mutableListOf() }.addAll(logs)
                }
                
                "数据准备完毕：${logBufferMap.size}个文件需要写入".println()

                logBufferMap.forEach { (file, logList) ->
                    doFileWrite(file, logList).call()
                }

                lastWriteTime = System.currentTimeMillis()
                "文件写入操作全部完成".println()
            }
        }
    }

    /**
     * 获取日志的文件前缀
     */
    private fun getFilePrefix(log: LogItem): String {
        val logTime = fileNameFormatter.get()!!.format(log.timeMillis)
        return if (InternalFileLogConfig.filePrefix.isNotEmpty()) {
            "${InternalFileLogConfig.filePrefix}-${logTime}"
        } else {
            logTime
        }
    }

    private fun doFileWrite(file: File, logList: List<LogItem>): Callable<Boolean> {

        return Callable {
            runCatching {
                if (!file.exists()) {
                    file.createNewFile()
                }
                "开始写入到文件:${file.name}".println()
                val sb = StringBuilder()
                logList.forEach { logItem ->
                    sb.append(
                        FileLogFormatter.formatToStr(logItem)
                    )
                }
                //将内容追加到文件中
                BufferedWriter(FileWriter(file, true)).use { bw ->
                    bw.append(sb.toString())
                    bw.flush()
                }
                "${file.name} 日志写入完成，条数：${logList.size}".println()
                sb.clear()
                true
            }.onFailure {
                "${file.name} 写入日志失败:${it.message}".println()
                it.printStackTrace()
            }.getOrDefault(false)
        }

    }

    /**
     * 清除过期的日志文件
     */

    private fun clearExpiredLogFiles() {
        "clearExpiredLogFiles storageDuration:${InternalFileLogConfig.storageDuration}".println()
        if (InternalFileLogConfig.storageDuration <= 0) {
            return
        }
        updateExistLogFileList()
        val hasDelete = AtomicBoolean(false)
        //计算过期时间点
        val expiredTime =
            System.currentTimeMillis() - InternalFileLogConfig.storageDuration * 60 * 60 * 1000
        "过期时间点:${
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                expiredTime
            )
        }".println()
        existLogFileList.filter {
            "${it.name} 最后修改时间:${
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                ).format(it.lastModified())
            }".println()

            it.isFile && it.lastModified() < expiredTime
        }.forEach {
            "删除过期日志文件:${it.name}".println()
            it.delete()
            hasDelete.set(true)
        }
        //重新获取一下文件列表
        if (hasDelete.get()) {
            updateExistLogFileList()
        }
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
    /**
     * 根据前缀获取操作文件（优化版本，避免重复遍历）
     */
    private fun getOperateFileByPrefix(filePrefix: String, sampleLog: LogItem): File {
        // 先检查缓存
        fileCache[filePrefix]?.let { cachedFile ->
            // 检查文件大小是否超限
            if (cachedFile.length() <= InternalFileLogConfig.maxFileSize) {
                return cachedFile
            }
        }

        // 获取以当前时间点开头的文件列表
        val existTimeFileList =
            existLogFileList.filter { it.name.startsWith(filePrefix) && it.name.endsWith(".txt") }

        var fileIndex = 1

        // 如果不存在以当前时间点开头的文件，则创建一个
        if (existTimeFileList.isEmpty()) {
            val newFile = File(logFileDir, "${filePrefix}-${fileIndex}.txt").also {
                it.createNewFile()
            }
            updateExistLogFileList()
            fileCache[filePrefix] = newFile
            return newFile
        }

        // 获取当前时间点文件名的最大index
        fileIndex = existTimeFileList.size
        val operateFile = File(logFileDir, "${filePrefix}-$fileIndex.txt")

        // 这里判断下如果不存在要创建一下，避免不可预知的错误
        if (!operateFile.exists()) {
            operateFile.createNewFile()
            updateExistLogFileList()
        }
        // 如果文件大小未超过限制，则直接返回
        if (operateFile.length() <= InternalFileLogConfig.maxFileSize) {
            fileCache[filePrefix] = operateFile
            return operateFile
        }

        // 创建一个新的文件
        fileIndex = existTimeFileList.size + 1
        val newFile = File(logFileDir, "${filePrefix}-${fileIndex}.txt").also {
            it.createNewFile()
        }
        updateExistLogFileList()
        fileCache[filePrefix] = newFile

        return newFile
    }



    private fun updateExistLogFileList() {
        lockBlock(updateFileLock) {
            "更新日志文件列表".println()
            existLogFileList = listLogDirFiles()
        }

    }


    override fun onAppExit() {
        super.onAppExit()
        println("FileLogWriter onAppExit 写入日志")
        writeLog(true)

        AppManager.removeAppStateListener(this)

    }


    private fun lockBlock(lock: ReentrantLock, timeoutSeconds: Long = 10, block: () -> Unit) {
        try {
            if (lock.tryLock(timeoutSeconds, TimeUnit.SECONDS)) {
                block()
            } else {
                "获取锁超时失败".println()
            }
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }

    }

}