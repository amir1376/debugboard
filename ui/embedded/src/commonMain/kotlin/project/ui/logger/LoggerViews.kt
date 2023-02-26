package project.ui.logger

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import project.LocalDebugBoard
import ir.amirab.debugboard.core.plugins.LogData
import ir.amirab.debugboard.core.plugins.Logger
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.amirab.debugboard.core.plugins.LogLevel
import java.util.Date

@Composable
fun rememberDebugBoardLoger(): Logger {
    return LocalDebugBoard.current.logger
}

class LoggerState {
    var filter: (LogData) -> Boolean by mutableStateOf(
        value = { true }
    )
    var showTags: Boolean by mutableStateOf(true)
    var showLogLevel: Boolean by mutableStateOf(true)
    var showDate: Boolean by mutableStateOf(true)
}


@OptIn(ExperimentalFoundationApi::class)
@Suppress("NAME_SHADOWING")
@Composable
fun LoggerView() {
    val state = remember { LoggerState() }
    val logger = rememberDebugBoardLoger()

    val logs by logger.logs.collectAsState()
    val (tagFilter, setTagFilter) = remember {
        mutableStateOf("")
    }
    val (messageFilter, setMessageFilter) = remember {
        mutableStateOf("")
    }
    LaunchedEffect(messageFilter, tagFilter) {
        state.filter = {
            val filterTag = if (tagFilter.isNotBlank()) {
                it.tag.contains(tagFilter)
            } else true
            val filterMessage = if (messageFilter.isNotBlank()) {
                it.message.contains(messageFilter)
            } else true
            filterTag && filterMessage
        }
    }
    Column {
        TextField(
            value = tagFilter,
            onValueChange = setTagFilter,
            placeholder = {
                Text("tag")
            },
        )
        TextField(
            value = messageFilter,
            onValueChange = setMessageFilter,
            placeholder = {
                Text("message...")
            },
        )
        val logsToShow by remember {
            derivedStateOf {
                logs.filter {
                    state.filter(it)
                }
            }
        }
        Logs(logsToShow, state)
    }

}

@Composable
fun Logs(logsToShow: List<LogData>, state: LoggerState) {
    LazyColumn {
        items(logsToShow) {
            RenderLog(
                it,
                state
            )
        }
    }
}

@Composable
fun RenderLog(
    logData: LogData,
    state: LoggerState
) {
    @Composable
    fun Space() {
        Spacer(Modifier.size(8.dp))
    }
    Row {
        if (state.showDate) {
            RenderDate(logData.time)
            Space()
        }
        if (state.showTags) {
            RenderTag(logData.tag)
            Space()
        }
        if (state.showLogLevel) {
            RenderLogLevel(logData.level)
            Space()
        }
        RenderLogMessage(logData.message)
    }
}

@Composable
fun RenderLogMessage(message: String) {
    Text(message)
}

@Composable
fun RenderLogLevel(level: LogLevel) {
    Text(
        level.toString(),
        color = when (level) {
            is LogLevel.Error -> MaterialTheme.colors.error
            is LogLevel.Warning -> Color(0xffcc7832)
            is LogLevel.Debug -> Color(0xffffc66d)
            else -> LocalContentColor.current
        }
    )
}

@Composable
fun RenderTag(tag: String) {
    Text("[${tag}]")
}

@Composable
fun RenderDate(time: Long) {
    Text(Date(time).toString())
}
