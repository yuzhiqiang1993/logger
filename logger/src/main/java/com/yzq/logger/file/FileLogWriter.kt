package com.yzq.logger.file

import com.yzq.application.AppStorage
import com.yzq.logger.data.LogItem
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors


/**
 * @description: 文件日志写入器
 * @author : yuzhiqiang
 */

internal class FileLogWriter private constructor(
    private val config: FileLogConfig
) {


    //日志文件的存储目录,默认是/data/user/0/com.xxx.xxxx/files/.log/
    private var logFileDir = ""

    //存放日志的缓冲map,key是要写入的文件，value是日志列表
    private val logBufferMap = ConcurrentHashMap<File, MutableList<LogItem>>()


    //最后一次写入的时间
    private var lastWriteTime = System.currentTimeMillis()

    private val scheduledExecutor = Executors.newSingleThreadScheduledExecutor()


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
        scheduledExecutor.scheduleAtFixedRate({
            println("定时任务开始执行:${config.writeLogInterval}")
            flushLog()
        }, config.writeLogInterval, config.writeLogInterval, java.util.concurrent.TimeUnit.SECONDS)


    }


    @Synchronized
    fun writeLog(log: LogItem) {
        

        //根据log的时间戳获取文件路径
        val operateFile = prepareOperateFile(log) ?: return
        println("writeLog operateFile = ${operateFile.name}")
        //把log存到logBufferMap中，有相同时间点的log则追加
        if (logBufferMap.containsKey(operateFile)) {
            logBufferMap[operateFile]?.add(log)
        } else {
            logBufferMap[operateFile] = mutableListOf(log)
        }
        flushLog()

    }

    @Synchronized
    private fun flushLog() {
        runCatching {
            println("flushLog logBufferMap.size = ${logBufferMap.size}")
            if (needWriteLog()) {
                println("满足写入条件，开始写入日志")
                //将logBufferMap中的日志遍历取出，对log进行格式化，然后写入到对应文件中去
                logBufferMap.iterator().forEach {
                    val file = it.key
                    if (file.exists()) {
                        val logList = it.value
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
                        lastWriteTime = System.currentTimeMillis()
                        println("${file.name} 日志写入完成，条数：${logList.size}")
                        sb.clear()
                        logList.clear()

                        logBufferMap.remove(file)
                    } else {
                        println("${file.name} 文件不存在")
                        //删除map中不存在的文件数据
                        logBufferMap.remove(file)
                    }

                }
            } else {
                println("不满足写入条件")
            }


        }.onFailure {
            it.printStackTrace()
        }

    }

    private fun needWriteLog(): Boolean {
        if (logBufferMap.isEmpty()) return false

        //获取map中所有log的数量
        val logCount = logBufferMap.values.sumOf { it.size }
        if (logCount <= 0) return false

        return if (logCount >= config.maxCacheSize) {
            println("日志条数超过最大缓存条数,开始写入")
            true
        } else {
            //计算距离最后一次写入的时间差，单位秒
            val timeGap = (System.currentTimeMillis() - lastWriteTime) / 1000
            if (timeGap >= config.writeLogInterval) {
                println("timeGap = $timeGap,超过写入间隔时间:${config.writeLogInterval}，开始写入")
                true
            } else {
                false
            }
        }
    }


    /**
     * 准备要操作的文件
     * @param log LogItem
     * @return File?
     */
    @Synchronized
    private fun prepareOperateFile(log: LogItem): File? {

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



        return getOperateFile(filePrefix)


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

        listLogDirFiles().filter {
            it.isFile && it.lastModified() < expiredTime
        }.forEach {
            println("删除过期日志文件:${it.name}")
            synchronized(it) {
                it.delete()
            }

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
    private fun getOperateFile(filePrefix: String): File? = runCatching {
        //获取以当前时间点开头的文件列表
        val existFileList =
            listLogDirFiles().filter { it.name.startsWith(filePrefix) && it.name.endsWith(".txt") }

        var fileIndex = 1

        //如果不存在以当前时间点开头的文件，则创建一个
        if (existFileList.isEmpty()) {
            println("不存在当前时间点的文件，新建")
            return File(logFileDir, "${filePrefix}-${fileIndex}.txt").also {
                it.createNewFile()
            }
        }

        //获取当前时间点文件名的最大index,实际上直接获取列表数量即可
        fileIndex = existFileList.size
        val operateFile = File(logFileDir, "${filePrefix}-$fileIndex.txt")

        //这里判断下如果不存在要创建一下，避免不可预知的错误
        if (!operateFile.exists()) {
            operateFile.createNewFile()
        }
        //如果文件大小未超过限制，则直接返回
        if (operateFile.length() <= config.maxFileSize) {
            return operateFile
        }

        //创建一个新的文件
        fileIndex = existFileList.size + 1
        File(logFileDir, "${filePrefix}-${fileIndex}.txt").also {
            it.createNewFile()
        }
    }.getOrDefault(null)


}