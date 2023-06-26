package ir.amirab.debugboard.ideaplugin.session

import com.intellij.openapi.project.Project
import ir.amirab.debugboard.api.models.ApiLogData
import ir.amirab.debugboard.api.models.ApiNetworkData
import ir.amirab.debugboard.api.models.ApiVariableInfo
import ir.amirab.debugboard.ideaplugin.DebugBoardConfig
import ir.amirab.debugboard.ideaplugin.utils.debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class Session(
    private val scope: CoroutineScope,
    private val config: DebugBoardConfig,
    val project: Project,
) :AutoCloseable{
    companion object{
        var count = 1
    }
    val id=count++

    var name = "session $id"

    private val _variableInfoFlow = MutableStateFlow<List<ApiVariableInfo>>(emptyList())
    val variableInfoFlow = _variableInfoFlow.asStateFlow()

    private val _networkInfoFlow = MutableStateFlow<List<ApiNetworkData>>(emptyList())
    val networkInfoFlow = _networkInfoFlow.asStateFlow()

    private val _logsDataFlow = MutableStateFlow<List<ApiLogData>>(emptyList())

    //not used yet!
    val logsDataFlow = _logsDataFlow.asStateFlow()

    private val backend = Backend(
        scope,
        _variableInfoFlow,
        _networkInfoFlow,
        _logsDataFlow
    )



    fun connect(address: String) {
        config.lastUsedHost = address
        backend.connect(address)
    }

    fun isConnectedOrConnecting(): Boolean {
        return backend.connectionState.value is ConnectionStatus.Connecting || backend.connectionState.value is ConnectionStatus.Connected
    }

    fun backendStatus(): MutableStateFlow<ConnectionStatus> {
        return backend.connectionState
    }

    fun disconnect() {
        backend.disconnect()
    }

    val setOpenedPaths = scope.debounce(50) { it: Set<List<String>> ->
        backend.setWatcherPaths(it)
    }

    override fun close() {
        disconnect()
        println("session closed")
    }
}