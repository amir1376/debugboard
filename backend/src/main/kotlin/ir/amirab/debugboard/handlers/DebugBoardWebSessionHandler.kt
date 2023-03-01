package ir.amirab.debugboard.handlers

import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.backend.commands.LoggerSubscribe
import ir.amirab.debugboard.backend.commands.LoggerUnSubscribe
import ir.amirab.debugboard.backend.commands.NetworkSubscribe
import ir.amirab.debugboard.backend.commands.NetworkUnSubscribe
import ir.amirab.debugboard.backend.commands.VariableWatcherSetOpenPaths
import ir.amirab.debugboard.backend.commands.WebsocketClientCommand
import ir.amirab.debugboard.core.plugin.logger.LogData
import ir.amirab.debugboard.core.plugin.network.FailResponse
import ir.amirab.debugboard.core.plugin.network.NetworkData
import ir.amirab.debugboard.core.plugin.network.SuccessResponse
import ir.amirab.debugboard.core.plugin.watcher.WatcherItem
import ir.amirab.debugboard.models.ApiFailResponse
import ir.amirab.debugboard.models.ApiLogData
import ir.amirab.debugboard.models.ApiNetworkData
import ir.amirab.debugboard.models.ApiRequest
import ir.amirab.debugboard.models.ApiSuccessResponse
import ir.amirab.debugboard.models.ApiVariableInfo
import ir.amirab.debugboard.models.response.LoggerOnNewData
import ir.amirab.debugboard.models.response.NetworkWatcherOnNewData
import ir.amirab.debugboard.models.response.VariableWatcherOnNewData
import ir.amirab.debugboard.models.response.WebsocketServerResponse
import java.lang.Exception

internal class DebugBoardWebSessionHandler(
    private val debugBoard: DebugBoard,
    private val json: Json,
    session: WebSocketSession
) : WebSocketSession by session {
    private var openedPaths = MutableStateFlow(
        setOf<List<String>>(
            emptyList()
        )
    )

    private suspend fun handleIncomingFrame(frame: Frame) {
//        println("new frame coming in $frame")
        if (frame !is Frame.Text) return
//        println("this is text frame")
        when (val command = getCommand(frame)) {
            is VariableWatcherSetOpenPaths -> {
                val newPaths = buildSet<List<String>> {
                    add(emptyList())
                    addAll(command.paths)
                }
//                println("update paths ${command.paths}")
                openedPaths.value = newPaths
            }

            LoggerSubscribe -> {
                sendLogs()
            }

            LoggerUnSubscribe -> {
                logJob?.cancel()
            }

            NetworkSubscribe -> {
                sendNetwork()
            }

            NetworkUnSubscribe -> {
                networkJob?.cancel()
            }

            else -> {
                println("$command not handled")
            }
        }
    }

    suspend fun handle() {
        autoSendWatchable()
        for (frame in incoming) {
            try {
                handleIncomingFrame(frame)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    private suspend fun sendNetwork() {
        val networkMonitor = debugBoard.networkMonitor
        networkJob?.cancelAndJoin()
        networkJob = launch {
            send(
                json.encodeToString<WebsocketServerResponse>(
                    NetworkWatcherOnNewData(
                        value = networkMonitor.records.value.map { mapNetworkInfoForApi(it) },
                        isThisInitialValue = true
                    )
                )
            )
            networkMonitor.updatedRecords.onEach {
                send(
                    json.encodeToString<WebsocketServerResponse>(
                        NetworkWatcherOnNewData(
                            value = listOf(mapNetworkInfoForApi(it)),
                            isThisInitialValue = false,
                        )
                    )
                )
            }.collect()
        }
    }


    private suspend fun sendWatchable(
        watcherItems: List<WatcherItem<*>>,
        openedPaths: Set<List<String>>
    ) {
        kotlin.runCatching {
            val data = watcherItems.map { watcherItem ->
                mapWatcherItemToVariableInfo(
                    watcherItem, emptyList(), openedPaths
                )
            }
//            println("sending new data")
            send(
                json.encodeToString<WebsocketServerResponse>(VariableWatcherOnNewData(data))
            )
//            println("sending done")
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    private suspend fun autoSendWatchable() {
        combine(
            debugBoard.variableWatcher.watcherItems,
            openedPaths
        ) { watcherItems, openPaths ->
            sendWatchable(watcherItems, openPaths)
        }.launchIn(this)
    }

    private var logJob: Job? = null
    private var networkJob: Job? = null
    private fun sendLogs() {
        val logger = debugBoard.logger
        logJob?.cancel()
        logJob = launch {
            send(
                json.encodeToString<WebsocketServerResponse>(
                    LoggerOnNewData(
                        logger.logs.value.map { mapLogForApi(it) },
                        true
                    )
                )
            )
            logger.newLogs.onEach {
                send(
                    json.encodeToString<WebsocketServerResponse>(
                        LoggerOnNewData(
                            listOf(mapLogForApi(it)),
                            false
                        )
                    )
                )
            }.collect()

        }

    }


    private fun getCommand(frame: Frame.Text): WebsocketClientCommand? {
        return kotlin.runCatching {
            json.decodeFromString<WebsocketClientCommand>(frame.readText())
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }
}


private fun mapNetworkInfoForApi(networkData: NetworkData): ApiNetworkData {
    return ApiNetworkData(
        tag = networkData.tag.hashCode().toString(),
        request = ApiRequest(
            url = networkData.request.url,
            method = networkData.request.method,
            headers = networkData.request.headers,
            body = networkData.request.body?.toString()
        ),
        response = networkData.response?.let {
            when (it) {
                is FailResponse -> ApiFailResponse(
                    it.cause.message ?: it::class.qualifiedName ?: "anonymous class exception"
                )

                is SuccessResponse -> ApiSuccessResponse(
                    code = it.code,
                    description = it.description,
                    headers = it.headers,
                    body = it.body?.toString()
                )
            }
        }
    )
}

private fun mapWatcherItemToVariableInfo(
    watcherItem: WatcherItem<*>,
    parentPath: List<String>,
    openedPaths: Set<List<String>>
): ApiVariableInfo {
    val currentPath = parentPath + watcherItem.name
    val shouldGetChildren = openedPaths.contains(currentPath)
//    println("paths are $openedPaths")
//    println("should (${currentPath.joinToString(".")}) have children ? $shouldGetChildren")

    return ApiVariableInfo(
        name = watcherItem.name,
        type = watcherItem.getType(),
        value = watcherItem.value.toString(),
        children = if (shouldGetChildren) watcherItem.children.map {
            mapWatcherItemToVariableInfo(it, currentPath, openedPaths)
        } else null
    )
}

private fun mapLogForApi(logData: LogData): ApiLogData {
    return ApiLogData(
        tag = logData.tag,
        level = logData.level.toString(),
        timestamp = logData.time,
        message = logData.message
    )

}