package com.yzq.logger_demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.logger.Logger
import com.yzq.logger.file.FileLogConfig
import com.yzq.logger.file.FileLogPrinter

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppManager.init(this, BuildConfig.DEBUG)


        val fileLogPrinter = FileLogPrinter.getInstance(
            FileLogConfig()
                .enable(true)
                .storageDuration(1)
//                        .showTimestamp(false)
//                        .showStackTrace(false)
        )


        Logger
//            .addPrinter(CustomerPrinter())
//            .addPrinter(
//                ConsoleLogPrinter.getInstance(
//                    ConsoleLogConfig.Builder().enable(true).lineLength(4000).build()
//                )
//            )
            .addPrinter(fileLogPrinter)


        Logger.i("Logger", "开始打印")
        Logger.it("customeTag", "asdsa", 112)


    }
}