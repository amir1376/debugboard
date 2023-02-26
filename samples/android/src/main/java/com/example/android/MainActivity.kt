package com.example.android

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import ir.amirab.debugboard.plugin.timber.DebugBoardTree
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.DebugBoardBackend
import timber.log.Timber
import kotlin.concurrent.thread

//
//class MainActivity:Activity() {
val db = DebugBoard()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this))


        Timber.plant(
            DebugBoardTree(db),
            Timber.DebugTree()
        )
        thread {
            while (true) {
                Timber.d("hello" + (1..1000).random())
                Thread.sleep(1000)
            }
        }
        thread {
            DebugBoardBackend(db).startWithDefaultServer()
        }
    }
}
