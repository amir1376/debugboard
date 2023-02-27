package ir.amirab.debugboard.plugin.network.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugins.NetworkMonitor
import ir.amirab.debugboard.core.plugins.Request
import ir.amirab.debugboard.core.plugins.SuccessResponse

private val RequestAndResponseTag = AttributeKey<Any>("RequestAndResponseTag")

@KtorDsl
class Config {
    private lateinit var debugBoard: DebugBoard

    internal val networkMonitor: NetworkMonitor get() = debugBoard.networkMonitor

    public fun debugBoard(board: DebugBoard) {
        debugBoard = board
    }
}

val KtorDebugBoard = createClientPlugin("KtorDebugBoard", ::Config) {
    val monitor = pluginConfig.networkMonitor
    on(SendingRequest) { request, content ->
        KtorIntegration.onRequest(monitor, request, content)
    }
    onResponse {
        KtorIntegration.onResponse(monitor, it.call, it)
    }
}
private object KtorIntegration {
    suspend fun onRequest(
        monitor: NetworkMonitor,
        request: HttpRequestBuilder,
        content: OutgoingContent,
    ) {
        val tag = Any()
        request.attributes.put(RequestAndResponseTag, tag)
        monitor.onNewRequest(
            tag,
            Request(
                url = request.url.buildString(),
                method = request.method.value,
                headers = request.headers.entries().associate { it.key to it.value },
                body = content.let {
                    if (it is TextContent) {
                        it.text
                    } else null
                }
            )
        )
    }

    suspend fun onResponse(
        monitor: NetworkMonitor,
        call: HttpClientCall,
        response: HttpResponse,
    ) {
        val tag = call.attributes[RequestAndResponseTag]
        monitor.onResponse(
            tag,
            SuccessResponse(
                code = response.status.value,
                description = response.status.description,
                headers = response.headers.entries().associate { it.key to it.value },
//                body = getResponseBodyAsText(response.contentType(), response.content)
                body = response.let {
                    when (it.contentType()) {
                        ContentType.Application.OctetStream -> "<stream data>"
                        else -> it.bodyAsText()
                    }
                }
            )
        )
    }
}


public fun HttpClientConfig<*>.debugBoardLogging(block: Config.() -> Unit = {}) {
    install(KtorDebugBoard, block)
}


