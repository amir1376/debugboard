package ir.amirab.debugboard.api.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ir.amirab.debugboard.api.models.ApiLogData
import ir.amirab.debugboard.api.models.ApiNetworkData
import ir.amirab.debugboard.api.models.ApiVariableInfo

@Serializable
sealed interface WebsocketServerResponse

@Serializable
@SerialName("VariableWatcher.onNewData")
data class VariableWatcherOnNewData(
    val value: List<ApiVariableInfo>
) : WebsocketServerResponse

@Serializable
@SerialName("NetworkWatcher.onNewData")
data class NetworkWatcherOnNewData(
    val value: List<ApiNetworkData>,
    val isThisInitialValue: Boolean,
) : WebsocketServerResponse


@Serializable
@SerialName("Logger.onNewData")
data class LoggerOnNewData(
    val value: List<ApiLogData>,
    val isThisInitialValue: Boolean,
) : WebsocketServerResponse

@Serializable
@SerialName("message")
data class MessageFromServer(
    val value: String
) : WebsocketServerResponse