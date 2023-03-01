package com.example.android

import android.app.Application
import android.util.Log
import ir.amirab.debugboard.DebugBoardBackend
import ir.amirab.debugboard.plugin.logger.timber.DebugBoardTree
import timber.log.Timber

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        DebugBoardBackend().startWithDefaultServer()
        Timber.plant(
            DebugBoardTree(),
            Timber.DebugTree()
        )
    }
}