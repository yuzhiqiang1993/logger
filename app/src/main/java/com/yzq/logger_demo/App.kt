package com.yzq.logger_demo

import android.app.Application
import com.yzq.logger.Logger
import com.yzq.logger.console.ConsoleLogConfig
import com.yzq.logger.console.ConsoleLogPrinter
import com.yzq.logger.file.FileLogConfig
import com.yzq.logger.file.FileLogPrinter
import com.yzq.logger_demo.customer.CustomerPrinter

class App : Application() {

    override fun onCreate() {
        super.onCreate()


        Logger
            .addPrinter(CustomerPrinter())
            .addPrinter(
                ConsoleLogPrinter.getInstance(
                    ConsoleLogConfig.Builder().enable(true).lineLength(4000).build()
                )
            )
            .addPrinter(FileLogPrinter.getInstance(FileLogConfig.Builder().enable(true).build()))


        Logger.i("Logger", "开始打印")
        Logger.it("customeTag", "asdsa", 112)


    }
}