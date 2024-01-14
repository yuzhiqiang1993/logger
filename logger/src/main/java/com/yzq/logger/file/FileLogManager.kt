package com.yzq.logger.file

import com.yzq.logger.data.LogItem
import java.io.File


/**
 * @description: 文件日志写入器
 * @author : yuzhiqiang
 */

internal class FileLogManager private constructor(
    private val config: FileLogConfig,
    private val formatter: FileLogFormatter
) {

    companion object {

        @Volatile
        var instance: FileLogManager? = null

        fun getInstance(
            config: FileLogConfig = FileLogConfig(),
            formatter: FileLogFormatter
        ): FileLogManager {
            return instance ?: synchronized(this) {
                instance ?: FileLogManager(config, formatter).also {
                    instance = it
                }
            }

        }

    }


    fun writeLogToFile(log: LogItem) {


        //写入文件
        val str = formatter.formatToStr(log)

        println(str)


//        //当前年-月-日-时
//        val logTime = SimpleDateFormat(
//            "yyyy-MM-dd-HH",
//            Locale.getDefault()
//        ).format(log.timeMillis)
//
//        //先确定文件名，规则为：文件名前缀-yyyy-MM-dd-HH-index.txt，例如：2024-01-14-14-1.txt
//        val filePrefix = if (config.filePrefix.isNotEmpty()) {
//            "${config.filePrefix}-${logTime}"
//        } else {
//            logTime
//        }
//        //获取要操作的文件
//        val operateFile = getOperateFile(filePrefix) ?: return


    }


    /**
     * 清除过期的日志文件
     */
    fun clearExpiredLogFiles() {
        if (config.storageDuration <= 0) {
            return
        }

        //计算过期时间点
        val expiredTime = System.currentTimeMillis() - config.storageDuration * 60 * 60 * 1000

        listLogDirFiles().filter {
            it.isFile && it.lastModified() < expiredTime
        }.forEach {
            it.delete()
        }

    }


    /**
     * 获取日志文件夹下的所有日志文件
     * @return List<File>
     */
    private fun listLogDirFiles(): List<File> {
        val logFileDir = File(config.logFilePath)
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

        //如果不存在以当前时间点开头的文件，则创建一个
        if (existFileList.isEmpty()) {
            return File(config.logFilePath, "${filePrefix}-1.txt").also {
                it.createNewFile()
            }
        }

        //获取当前时间点文件名的最大index
        val maxIndex = maxIndex(filePrefix, existFileList)
        val operateFile = File("${filePrefix}-$maxIndex.txt")

        if (operateFile.length() <= config.maxFileSize) {
            return operateFile
        }

        //计算index
        val newIndex = maxIndex + 1
        File("${filePrefix}-${newIndex}.txt").also {
            it.createNewFile()
        }
    }.getOrDefault(null)


    /**
     * 获取当前时间点文件名的最大index
     * @param filePrefix String
     * @return Int
     */
    private fun maxIndex(filePrefix: String, fileList: List<File>): Int {
        return fileList
            .map { it.getFileIndex(filePrefix) }
            .max()
    }

    /**
     * 获取文件名的index
     * @receiver File
     * @param filePrefix String
     * @return Int
     */
    private fun File.getFileIndex(filePrefix: String): Int =
        runCatching {
            var index = 1
            if (this.isFile && this.name.startsWith("${filePrefix}-") && this.name.endsWith(".txt")) {
                index = this.name.substringAfter("${filePrefix}-").substringBefore(".txt").toInt()
            }
            index

        }.getOrDefault(1)

}