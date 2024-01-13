package com.yzq.logger.demo

import android.app.Application
import com.yzq.logger.Logger
import com.yzq.logger.LoggerConfig
import com.yzq.logger.demo.customer.CustomerPrinter
import com.yzq.logger.printer.ConsoleLogPrinter
import com.yzq.logger.printer.FileLogPrinter

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger
            .addPrinter(CustomerPrinter().apply {
                loggerConfig = LoggerConfig.Builder()
                    .enable(true)
                    .tag("CustomerPrinter")
                    .showBorder(false)
                    .build()
            })
            .addPrinter(ConsoleLogPrinter.instance.apply {
                loggerConfig = LoggerConfig.Builder()
                    .enable(true)
                    .tag("ConsoleLogPrinter")
                    .showBorder(true)
                    .build()

            })
            .addPrinter(FileLogPrinter.instance.apply {
                loggerConfig = LoggerConfig.Builder()
                    .enable(false)
                    .tag("FileLogPrinter")
                    .showBorder(true)
                    .build()
            })

        Logger.i("Logger", "开始打印")
        Logger.it("customeTag", "asdsa", 112)


    }
}