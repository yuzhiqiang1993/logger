package com.yzq.logger_demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.logger.Logger
import com.yzq.logger.console.ConsoleLogConfig
import com.yzq.logger.console.ConsoleLogPrinter
import com.yzq.logger.core.loggerDebug

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppManager.init(this, BuildConfig.DEBUG)
        loggerDebug = true



        Logger
//            .addPrinter(
//                FileLogPrinter.getInstance(
//                    FileLogConfig.Builder()
//                        .enable(true)
//                        .storageDuration(1)
////                .showStackTrace(false)
//                        .writeLogInterval(20)
//                        .maxFileSize(1024 * 1024 * 2)//2M
////                        .showTimestamp(false)
////                        .showStackTrace(false)
//                        .build()
//                )
//            )
            .addPrinter(
                ConsoleLogPrinter.getInstance(
                    ConsoleLogConfig.Builder().enable(BuildConfig.DEBUG).build()
                )
            )



        Logger.i("Logger", "开始打印")
        Logger.it("customeTag", "asdsa", 112)


    }
}