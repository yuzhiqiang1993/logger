package com.yzq.logger_demo

import android.app.Application
import com.yzq.application.AppManager
import com.yzq.coroutine.interval.interval
import com.yzq.logger.Logger
import com.yzq.logger.console.ConsoleLogConfig
import com.yzq.logger.console.ConsoleLogPrinter
import com.yzq.logger.file.FileLogConfig
import com.yzq.logger.file.FileLogPrinter
import com.yzq.logger.view.core.ViewLogConfig
import com.yzq.logger.view.core.ViewLogPrinter

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppManager.init(this, BuildConfig.DEBUG)


        val consoleLogPrinter = ConsoleLogPrinter.getInstance(
            ConsoleLogConfig.Builder().enable(true).showStackTrace(true).showThreadInfo(true)
                .lineLength(1000).showBorder(true).tag("customeTag").build()
        )
        val fileLogPrinter = FileLogPrinter.getInstance(
            FileLogConfig.Builder().enable(true).writeLogInterval(10).logCapacity(100)
                .memoryCacheSize(100).build()
        )


        val viewLogPrinter = ViewLogPrinter.getInstance(
            ViewLogConfig.Builder().enable(true).showStackTrace(false).showThreadInfo(true)
                .cacheSize(10).build()
        )


        Logger.addPrinter(consoleLogPrinter).addPrinter(fileLogPrinter).addPrinter(viewLogPrinter)
            .debug(true)


        Logger.i("Logger", "开始打印不同等级的日志")
        Logger.v("customeTag", "asdsa", 112)
        Logger.i("customeTag", "asdsa", 112)
        Logger.d("customeTag", "asdsa", 112)
        Logger.e("customeTag", "asdsa", 112)
        Logger.w("customeTag", "asdsa", 112)
        Logger.wtf("customeTag", "asdsa", 112)


        interval().subscribe {
            if ((it % 2).toInt() == 0) {
                Logger.i("interval", "interval:${it}")
            } else {
                Logger.e("interval", "interval:${it}")

            }
        }.start()


    }
}