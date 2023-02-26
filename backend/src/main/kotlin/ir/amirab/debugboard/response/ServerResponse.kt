package ir.amirab.debugboard.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ir.amirab.debugboard.models.ApiLogData
import ir.amirab.debugboard.models.ApiNetworkData
import ir.amirab.debugboard.models.ApiVariableInfo

@Serializable
internal sealed interface WebsocketServerResponse

@Serializable
@SerialName("VariableWatcher.onNewData")
internal data class VariableWatcherOnNewData(
    val value: List<ApiVariableInfo>
) : WebsocketServerResponse

@Serializable
@SerialName("NetworkWatcher.onNewData")
internal data class NetworkWatcherOnNewData(
    val value: List<ApiNetworkData>,
    val isThisInitialValue: Boolean,
) : WebsocketServerResponse


@Serializable
@SerialName("Logger.onNewData")
internal data class LoggerOnNewData(
    val value: List<ApiLogData>,
    val isThisInitialValue: Boolean,
) : WebsocketServerResponse

@Serializable
@SerialName("message")
internal data class MessageFromServer(
    val value: String
) : WebsocketServerResponse