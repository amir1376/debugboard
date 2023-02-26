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

private val DisableMonitoring = AttributeKey<Unit>("DisableMonitoring")
private val RequestAndResponseTag = AttributeKey<Any>("RequestAndResponseTag")

@KtorDsl
public class Config {
    /**
     * filters
     */
    internal var filters = mutableListOf<(HttpRequestBuilder) -> Boolean>()

    internal lateinit var debugBoard: DebugBoard

    internal val networkMonitor: NetworkMonitor get() = debugBoard.networkMonitor

    public fun debugBoard(board: DebugBoard) {
        debugBoard = board
    }

    /**
     * Allows you to filter log messages for calls matching a [predicate].
     */
    public fun filter(predicate: (HttpRequestBuilder) -> Boolean) {
        filters.add(predicate)
    }
}

public class KtorDebugBoard private constructor(
    private val filters: List<(HttpRequestBuilder) -> Boolean>,
    private val networkMonitor: NetworkMonitor
) {

    private fun setupRequestMonitoring(client: HttpClient) {
        client.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
            if (!shouldBeMonitored(context)) {
                context.attributes.put(DisableMonitoring, Unit)
                return@intercept
            }
            val response = try {
                KtorIntegration.onRequest(networkMonitor, context, it as OutgoingContent)
            } catch (_: Throwable) {
                null
            }

            try {
                proceedWith(subject)
            } catch (cause: Throwable) {
                throw cause
            } finally {
            }
        }
    }

    private fun setupResponseMonitoring(client: HttpClient) {
        val observer: ResponseHandler = observer@{
            if (it.call.attributes.contains(DisableMonitoring)) {
                return@observer
            }
            KtorIntegration.onResponse(
                networkMonitor, it.call, it
            )
        }

        ResponseObserver.install(ResponseObserver(observer), client)
    }


    private fun shouldBeMonitored(request: HttpRequestBuilder): Boolean =
        filters.isEmpty() || filters.any { it(request) }

    companion object : HttpClientPlugin<Config, KtorDebugBoard> {
        override val key: AttributeKey<KtorDebugBoard> = AttributeKey("KtorDebugBoard")

        override fun prepare(block: Config.() -> Unit): KtorDebugBoard {
            val config = Config().apply(block)
            return KtorDebugBoard(config.filters, config.networkMonitor)
        }

        override fun install(plugin: KtorDebugBoard, scope: HttpClient) {
            plugin.setupRequestMonitoring(scope)
            plugin.setupResponseMonitoring(scope)
        }
    }
}

val KtorDebugBoard2 = createClientPlugin("KtorDebugBoard", ::Config) {
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


