package com.yzq.logger_demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.coroutine.interval.interval
import com.yzq.logger.Logger
import com.yzq.logger.console.ConsoleLogConfig
import com.yzq.logger.console.ConsoleLogPrinter
import com.yzq.logger.core.loggerDebug
import com.yzq.logger.file.FileLogConfig
import com.yzq.logger.file.FileLogPrinter
import com.yzq.logger.view.core.ViewLogConfig
import com.yzq.logger.view.core.ViewLogPrinter

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppManager.init(this, BuildConfig.DEBUG)
        loggerDebug = true

//        val fileLogPrinter = FileLogPrinter.getInstance(
//            FileLogConfig.Builder()
//                .enable(true)
//                .storageDuration(1)
////                .showStackTrace(false)
//                .writeLogInterval(20)
//                .maxFileSize(1024 * 1024 * 2)//2M
////                        .showTimestamp(false)
////                        .showStackTrace(false)
//                .build()
//        )


        val viewLogPrinter = ViewLogPrinter.getInstance(
            ViewLogConfig.Builder().enable(true).showStackTrace(false).showThreadInfo(true)
                .cacheSize(10).build()
        )


        val consoleLogConfig = ConsoleLogConfig.Builder().enable(true)
            .showStackTrace(true)
            .showThreadInfo(true)
            .lineLength(1000)
            .showBorder(true)
            .tag("customeTag")
            .build()

        Logger
            .addPrinter(
                FileLogPrinter.getInstance(
                    FileLogConfig.Builder().enable(true)
                        .writeLogInterval(10)
                        .logCapacity(100)
                        .memoryCacheSize(100)
                        .build()
                )
            )
            .addPrinter(viewLogPrinter)
            .addPrinter(
                ConsoleLogPrinter.getInstance(consoleLogConfig)
            )



        Logger.i("Logger", "开始打印不同等级的日志")
        Logger.vt("customeTag", "asdsa", 112)
        Logger.it("customeTag", "asdsa", 112)
        Logger.dt("customeTag", "asdsa", 112)
        Logger.et("customeTag", "asdsa", 112)
        Logger.wt("customeTag", "asdsa", 112)
        Logger.wtft("customeTag", "asdsa", 112)


        interval().subscribe {
            if ((it % 2).toInt() == 0) {
                Logger.it("interval", "interval:${it}")
            } else {
                Logger.et("interval", "interval:${it}")

            }
        }.start()


    }
}