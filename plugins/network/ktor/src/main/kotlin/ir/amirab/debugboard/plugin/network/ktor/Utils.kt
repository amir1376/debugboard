package ir.amirab.debugboard.plugin.network.ktor

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.network.FailResponse
import ir.amirab.debugboard.core.plugin.network.Request
import ir.amirab.debugboard.core.plugin.network.SuccessResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import java.lang.Exception
import kotlin.text.Charsets

internal class KtorTransaction(
    private val debugBoard: DebugBoard
) {
    private val tag = Any()


    fun onRequestReady() {
        debugBoard.networkMonitor.onNewRequest(
            tag,
            Request(
                headers = requestHeaders,
                method = method,
                url = url,
                body = requestBody
            )
        )
    }

    fun onError(cause: Throwable) {
        debugBoard.networkMonitor.onResponse(
            tag,
            FailResponse(
                cause.let {
                    if (it is Exception) {
                        it
                    } else {
                        Exception(it)
                    }
                }
            )
        )
    }

    fun onSuccessResponseReady() {
        debugBoard.networkMonitor.onResponse(
            tag,
            SuccessResponse(
                code = code,
                description = description!!,
                headers = responseHeaders,
                body = responseBody
            )
        )
    }


    lateinit var url: String
    lateinit var method: String
    lateinit var requestHeaders: Map<String, List<String>>
    var requestBody: String? = null


    var code: Int = -1
    var description: String? = null
    lateinit var responseHeaders: Map<String, List<String>>
    var responseBody: String? = null
}

internal suspend inline fun ByteReadChannel.tryReadText(charset: Charset): String? = try {
    readRemaining().readText(charset = charset)
} catch (cause: Throwable) {
    null
}

internal suspend fun OutgoingContent.observe(log: ByteWriteChannel): OutgoingContent = when (this) {
    is OutgoingContent.ByteArrayContent -> {
        log.writeFully(bytes())
        log.close()
        this
    }

    is OutgoingContent.ReadChannelContent -> {
        val responseChannel = ByteChannel()
        val content = readFrom()

        content.copyToBoth(log, responseChannel)
        LoggedContent(this, responseChannel)
    }

    is OutgoingContent.WriteChannelContent -> {
        val responseChannel = ByteChannel()
        val content = toReadChannel()
        content.copyToBoth(log, responseChannel)
        LoggedContent(this, responseChannel)
    }

    else -> {
        log.close()
        this
    }
}

internal class LoggedContent(
    private val originalContent: OutgoingContent,
    private val channel: ByteReadChannel
) : OutgoingContent.ReadChannelContent() {

    override val contentType: ContentType? = originalContent.contentType
    override val contentLength: Long? = originalContent.contentLength
    override val status: HttpStatusCode? = originalContent.status
    override val headers: Headers = originalContent.headers

    override fun <T : Any> getProperty(key: AttributeKey<T>): T? = originalContent.getProperty(key)

    override fun <T : Any> setProperty(key: AttributeKey<T>, value: T?) =
        originalContent.setProperty(key, value)

    override fun readFrom(): ByteReadChannel = channel
}

@OptIn(DelicateCoroutinesApi::class)
private fun OutgoingContent.WriteChannelContent.toReadChannel(): ByteReadChannel =
    GlobalScope.writer(Dispatchers.Default) {
        writeTo(channel)
    }.channel

internal suspend fun captureResponseBody(
    transactionBuilder: KtorTransaction,
    contentType: ContentType?,
    content: ByteReadChannel
) {
    val message =
        content.tryReadText(contentType?.charset() ?: Charsets.UTF_8) ?: "[response body omitted]"
    transactionBuilder.responseBody = message
}