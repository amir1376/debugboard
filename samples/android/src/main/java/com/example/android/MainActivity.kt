package com.example.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ir.amirab.debugboard.plugin.watcher.compose.AddWatch

/**
 * A simple android activity to use Debug Board to watch compose state variables
 * for see more features please see desktop sample
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val (count,setCount)=remember{
                mutableStateOf(1)
            }
            Column {
                Row{
                    Button({setCount(count+1)}){
                        Text("+")
                    }
                    Button({setCount(count-1)}){
                        Text("-")
                    }
                }
                repeat(count){
                    TextFieldView(it)
                }
            }
        }
    }
}
@Composable
fun TextFieldView(index:Int){
    val (text,setText) = remember { mutableStateOf("") }
    AddWatch("text$index",text)
    TextField(text, onValueChange = {
        setText(it)
    })
}