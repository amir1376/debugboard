package ir.amirab.debugboard.ideaplugin

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
import io.ktor.client.*
import ir.amirab.debugboard.api.models.ApiLogData
import ir.amirab.debugboard.api.models.ApiNetworkData
import ir.amirab.debugboard.api.models.ApiVariableInfo
import ir.amirab.debugboard.ideaplugin.utils.debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@State(name = "DebugBoardConfig")
@Service(Service.Level.PROJECT)
class DebugBoardService(
    val project: Project
) : Disposable, PersistentStateComponent<DebugBoardConfig> {
    var config = DebugBoardConfig()
    val scope = CoroutineScope(SupervisorJob())

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

    init {
        bootstrap()
    }

    private fun bootstrap() {

    }

    override fun dispose() {
        scope.cancel()
    }


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

    override fun getState(): DebugBoardConfig? {
        return config
    }

    override fun loadState(state: DebugBoardConfig) {
        config = state
    }
}