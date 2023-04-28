package ir.amirab.debugboard.plugin.network.ktor

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*
import ir.amirab.debugboard.core.DebugBoard
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.text.Charsets

class KtorDebugBoard private constructor(
    private val debugBoard: DebugBoard
) {
    private fun setupRequestCapturing(client: HttpClient) {
        client.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
            val transactionBuilder = KtorTransaction(debugBoard)
            context.attributes.put(RequestAndResponseTransaction, transactionBuilder)

            val response = try {
                captureRequest(context, transactionBuilder)
            } catch (_: Throwable) {
                null
            }

            try {
                proceedWith(response ?: subject)
            } catch (cause: Throwable) {
                transactionBuilder.onError(cause)
                throw cause
            }
        }
    }

    private suspend fun captureRequest(
        request: HttpRequestBuilder,
        transactionBuilder: KtorTransaction
    ): OutgoingContent {
        val content = request.body as OutgoingContent

        transactionBuilder.url = request.url.build().toString()
        transactionBuilder.method = request.method.value
        transactionBuilder.requestHeaders = request.headers.entries().associate {
            it.key to it.value
        }

        return captureRequestBody(content, transactionBuilder)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun captureRequestBody(
        content: OutgoingContent,
        transactionBuilder: KtorTransaction
    ): OutgoingContent {
        val charset = content.contentType?.charset() ?: Charsets.UTF_8
        val channel = ByteChannel()
        GlobalScope.launch(Dispatchers.Unconfined) {
            val text = channel.tryReadText(charset)
            transactionBuilder.requestBody = text
            transactionBuilder.onRequestReady()
        }

        return content.observe(channel)
    }


    @OptIn(InternalAPI::class)
    private fun setupResponseCapturing(client: HttpClient) {
        client.receivePipeline.intercept(HttpReceivePipeline.State) { response ->

            val transaction = response.call.attributes[RequestAndResponseTransaction]

            try {
                val callResponse = response.call.response
                transaction.responseHeaders = callResponse.headers.entries().associate {
                    it.key to it.value
                }
                transaction.code = callResponse.status.value
                transaction.description = callResponse.status.description

                proceedWith(subject)
            } catch (cause: Throwable) {
                transaction.onError(cause)
                throw cause
            }
        }

        client.responsePipeline.intercept(HttpResponsePipeline.Receive) {
            try {
                proceed()
            } catch (cause: Throwable) {
                val transaction = context.attributes[RequestAndResponseTransaction]
                transaction.onError(cause)
                throw cause
            }
        }

        val observer: ResponseHandler = observer@{

            val transaction = it.call.attributes[RequestAndResponseTransaction]
            try {
                captureResponseBody(transaction, it.contentType(), it.content)
            } catch (_: Throwable) {
            } finally {
                transaction.onSuccessResponseReady()
            }
        }

        ResponseObserver.install(ResponseObserver(observer), client)
    }

    companion object : HttpClientPlugin<Config, KtorDebugBoard> {
        override val key: AttributeKey<KtorDebugBoard> = AttributeKey("DebugBoard")

        override fun prepare(block: Config.() -> Unit): KtorDebugBoard {
            val config = Config().apply(block)
            return KtorDebugBoard(config.debugBoard)
        }

        override fun install(plugin: KtorDebugBoard, scope: HttpClient) {
            plugin.setupRequestCapturing(scope)
            plugin.setupResponseCapturing(scope)
        }
    }
}

@KtorDsl
class Config internal constructor() {
    var debugBoard: DebugBoard = DebugBoard.Default
}

private val RequestAndResponseTransaction =
    AttributeKey<KtorTransaction>("RequestAndResponseTransaction")
