package ir.amirab.debugboard.api.models.commands

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface WebsocketClientCommand {
//    val type:String
}


@Serializable
@SerialName("VariableWatcher.setOpenPaths")
data class VariableWatcherSetOpenPaths(
    val paths: Set<List<String>>
) : WebsocketClientCommand

@Serializable
@SerialName("Logger.subscribe")
object LoggerSubscribe : WebsocketClientCommand

@Serializable
@SerialName("Logger.unsubscribe")
object LoggerUnSubscribe : WebsocketClientCommand

@Serializable
@SerialName("Network.subscribe")
object NetworkSubscribe : WebsocketClientCommand

@Serializable
@SerialName("Network.unsubscribe")
object NetworkUnSubscribe : WebsocketClientCommand


