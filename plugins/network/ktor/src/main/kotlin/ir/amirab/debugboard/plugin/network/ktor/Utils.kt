package ir.amirab.debugboard.plugin.network.ktor

import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*

internal fun Appendable.logHeader(key: String, value: String) {
    appendLine("-> $key: $value")
}

internal suspend inline fun ByteReadChannel.tryReadText(charset: Charset): String? = try {
    readRemaining().readText(charset = charset)
} catch (cause: Throwable) {
    null
}

internal suspend fun getResponseBodyAsText(
    contentType: ContentType?,
    content: ByteReadChannel
): String {
    return content.tryReadText(contentType?.charset() ?: Charsets.UTF_8) ?: "[response body omitted]"
}
