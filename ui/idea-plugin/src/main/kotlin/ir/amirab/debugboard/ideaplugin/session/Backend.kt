package ir.amirab.debugboard.ideaplugin.session

import com.intellij.openapi.diagnostic.thisLogger
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import ir.amirab.debugboard.api.models.ApiLogData
import ir.amirab.debugboard.api.models.ApiNetworkData
import ir.amirab.debugboard.api.models.ApiVariableInfo
import ir.amirab.debugboard.api.models.commands.NetworkSubscribe
import ir.amirab.debugboard.api.models.commands.VariableWatcherSetOpenPaths
import ir.amirab.debugboard.api.models.commands.WebsocketClientCommand
import ir.amirab.debugboard.api.models.response.*
import ir.amirab.debugboard.ideaplugin.utils.addOrReplace
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

private val client = HttpClient {
    install(WebSockets)
}

sealed interface ConnectionStatus {
    object IDLE : ConnectionStatus
    data class Connecting(val address: String) : ConnectionStatus
    data class Connected(val address: String) : ConnectionStatus
}

class Backend(
    private val scope: CoroutineScope,
    private val variableInfoFlow: MutableStateFlow<List<ApiVariableInfo>>,
    private val networkInfoFlow: MutableStateFlow<List<ApiNetworkData>>,
    private val logsDataFlow: MutableStateFlow<List<ApiLogData>>
) {
    val connectionState = MutableStateFlow<ConnectionStatus>(ConnectionStatus.IDLE)

    private var wsJob: Job? = null

    fun setWatcherPaths(paths: Set<List<String>>) {
        send(VariableWatcherSetOpenPaths(paths))
    }


    private fun send(command: WebsocketClientCommand) {
        outgoingFrames.trySend(command)
    }


    private val outgoingFrames = Channel<WebsocketClientCommand>(
        12,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )


    private fun initiateWebsocket(
        address: String
    ): Job {
        return scope.launch {
            while (isActive) {
                connectionState.value = ConnectionStatus.Connecting(address)
                kotlin.runCatching {
                    client.webSocket(address) {
                        thisLogger().info("connected!")
                        connectionState.value = ConnectionStatus.Connected(address)
                        val receiveJob = incoming.receiveAsFlow()
                            .filterIsInstance<Frame.Text>()
                            .onEach {
                                handleFrames(
                                    json.decodeFromString(it.readText())
                                )
                            }.launchIn(this)
                        outgoingFrames.receiveAsFlow()
                            .onEach {
                                send(json.encodeToString(it))
                            }.launchIn(this)
//                        we don't have Logger view yet!
//                        send(LoggerSubscribe)
                        send(NetworkSubscribe)
                        receiveJob.join()
                    }
                }.onFailure {
                    it.printStackTrace()
                }
                thisLogger().info("retrying in 1000ms")
                delay(1000)
            }
        }
    }

    fun connect(address: String) {
        disconnect()
        wsJob = initiateWebsocket(address)
    }

    fun clearData() {
        logsDataFlow.value = emptyList()
        variableInfoFlow.value = emptyList()
        networkInfoFlow.value = emptyList()
    }

    fun disconnect() {
        wsJob?.cancel()
        connectionState.value = ConnectionStatus.IDLE
        wsJob = null
        clearData()
    }


    private fun handleFrames(receive: WebsocketServerResponse) {
        when (receive) {
            is LoggerOnNewData -> {
                if (receive.isThisInitialValue) {
                    logsDataFlow.value = receive.value
                } else {
                    logsDataFlow.value = logsDataFlow.value + logsDataFlow.value
                }
            }

            is MessageFromServer -> {
                thisLogger().info(receive.value)
            }

            is NetworkWatcherOnNewData -> {
                if (receive.isThisInitialValue) {
                    networkInfoFlow.value = receive.value
                } else {
                    networkInfoFlow.value = networkInfoFlow.value
                        .toMutableList()
                        .apply {
                            addOrReplace(receive.value) { it.tag }
                        }.toList()
                }
            }

            is VariableWatcherOnNewData -> {
                variableInfoFlow.value = receive.value
            }
        }
    }
}