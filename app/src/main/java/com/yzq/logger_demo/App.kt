package com.yzq.logger_demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.logger.Logger
import com.yzq.logger.core.loggerDebug
import com.yzq.logger.file.FileLogConfig
import com.yzq.logger.file.FileLogPrinter

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppManager.init(this, BuildConfig.DEBUG)
        loggerDebug = true
        val fileLogPrinter = FileLogPrinter.getInstance(
            FileLogConfig().enable(true)
                .storageDuration(1)
//                .showStackTrace(false)
                .writeLogInterval(20)
                .maxFileSize(1024 * 1024 * 2)//2M
//                        .showTimestamp(false)
//                        .showStackTrace(false)
        )


        Logger.addPrinter(fileLogPrinter)



        Logger.i("Logger", "开始打印")
        Logger.it("customeTag", "asdsa", 112)


    }
}