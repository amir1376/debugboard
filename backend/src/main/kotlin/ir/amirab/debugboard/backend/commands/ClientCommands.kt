package ir.amirab.debugboard.backend.commands

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface WebsocketClientCommand {
//    val type:String
}


@Serializable
@SerialName("VariableWatcher.setOpenPaths")
internal data class VariableWatcherSetOpenPaths(
    val paths: Set<List<String>>
) : WebsocketClientCommand

@Serializable
@SerialName("Logger.subscribe")
internal object LoggerSubscribe : WebsocketClientCommand

@Serializable
@SerialName("Logger.unsubscribe")
internal object LoggerUnSubscribe : WebsocketClientCommand

@Serializable
@SerialName("Network.subscribe")
internal object NetworkSubscribe : WebsocketClientCommand

@Serializable
@SerialName("Network.unsubscribe")
internal object NetworkUnSubscribe : WebsocketClientCommand


