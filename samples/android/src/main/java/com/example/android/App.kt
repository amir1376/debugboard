package com.example.android

import android.app.Application
import ir.amirab.debugboard.backend.DebugBoardBackend
import ir.amirab.debugboard.plugin.logger.timber.DebugBoardTree
import timber.log.Timber

class App : Application(){
    override fun onCreate() {
        super.onCreate()

        // make sure to add internet permission (at least for debug)
        DebugBoardBackend().startWithDefaultServer()
        Timber.plant(
            // plant DebugBoardTree for see timber logs in panel
            DebugBoardTree(),
            Timber.DebugTree()
        )
    }
}