package com.yzq.logger.demo

import android.app.Application
import com.yzq.logger.LogConfig
import com.yzq.logger.Logger
import com.yzq.logger.demo.customer.CustomerPrinter
import com.yzq.logger.printer.ConsoleLogPrinter

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val logConfig =
            LogConfig.Builder()
                .enable(BuildConfig.DEBUG)
                .globalTag("LoggerDemo")
                .showBorder(true)
                .addPrinter(CustomerPrinter())//定义定的打印器
                .addPrinter(ConsoleLogPrinter.instance)
//                .addPrinter(ConsoleLogPrinter.instance.apply {
//                    formatter = CustomerFormater//定义自定义的格式化器
//                })
                .build()

        Logger.init(logConfig)
        Logger.i(123, logConfig)
        Logger.it(tag = "customeTag", "asdsa", 112)

    }
}