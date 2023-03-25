package com.example.android

import android.app.Application
import ir.amirab.debugboard.backend.DebugBoardBackend
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