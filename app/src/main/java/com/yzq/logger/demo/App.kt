package com.yzq.logger.demo

import android.app.Application
import com.yzq.logger.Logger

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.setDebug(BuildConfig.DEBUG, "LoggerDemo")
        Logger.i("App onCreate")
    }
}