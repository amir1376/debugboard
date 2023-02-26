package ir.amirab.debugboard.plugin.network.okhttp

import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.*
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugins.FailResponse
import ir.amirab.debugboard.core.plugins.SuccessResponse
import java.lang.Exception
import java.nio.charset.Charset

internal class OkhttpRequestResponseProcessor(
    private val debugBoard: DebugBoard,
    private val maxContentLength: Long
) {
    private val tag = Any()

    fun processRequest(request: Request) {
        val requestBody = request.body
        val hasRequestBody = requestBody != null
        val reqMethod = request.method
        val reqUrl = request.url.toString()
        val reqHeaders = request.headers.toMultimap()
        val reqContentType: MediaType?
        var requestBodyString: String? = null
        val requestBodyIsPlainText = bodyHasSupportedEncoding(request.headers)
        if (hasRequestBody && requestBodyIsPlainText) {
            val source = getNativeSource(Buffer(), bodyGzipped(request.headers))
            val buffer = source.buffer
            requestBody!!.writeTo(buffer)
            var charset = Charset.forName("utf-8")
            reqContentType = requestBody.contentType()
            if (reqContentType != null) {
                charset = reqContentType.charset(charset)!!
            }
            if (isPlaintext(buffer)) {
                requestBodyString = readFromBuffer(buffer, charset)
            }
        }
        debugBoard.networkMonitor.onNewRequest(
            tag = tag,
            request = ir.amirab.debugboard.core.plugins.Request(
                headers = reqHeaders,
                method = reqMethod,
                url = reqUrl,
                body = requestBodyString
            )
        )
    }

    fun processResponse(response: Response) {
        val responseBody = response.body
        val resCode = response.code
        val resDescription = response.message
        val resHeaders = response.headers.toMultimap()
        val responseBodyIsPlainText = bodyHasSupportedEncoding(response.headers)
        var resBodyString: String? = null
        if (response.promisesBody() && responseBodyIsPlainText) {
            val source = getNativeSource(response)
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            var charset = Charset.forName("UTF-8")
            val resContentType = responseBody!!.contentType()
            if (resContentType != null) {
//                charset = try {
                charset = resContentType.charset(charset)!!
//                } catch (e: UnsupportedCharsetException) {
//                    update()
//                    return response
//                }
            }
            resBodyString = if (isPlaintext(buffer)) {
                (readFromBuffer(buffer.clone(), charset))
            } else {
                null
            }
        }
        debugBoard.networkMonitor.onResponse(
            tag = tag,
            response = SuccessResponse(
                code = resCode,
                description = resDescription,
                headers = resHeaders,
                body = resBodyString
            )
        )
    }

    fun processResponse(exception: Exception) {
        debugBoard.networkMonitor.onResponse(tag, FailResponse(exception))
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }

    private fun bodyHasSupportedEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding
            ?.lowercase()
            ?.let {
                it == "identity" || it == "gzip"
            } ?: true
    }

    private fun bodyGzipped(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]?.lowercase()
        return contentEncoding == "gzip"
    }

    private fun readFromBuffer(buffer: Buffer, charset: Charset?): String {
        val bufferSize = buffer.size
        val maxBytes = bufferSize.coerceAtMost(maxContentLength)
        var body = ""
        try {
            body = buffer.readString(maxBytes, charset!!)
        } catch (e: EOFException) {
            body += "<chuck_body_unexpected_eof>"
        }
        if (bufferSize > maxContentLength) {
            body += "<chuck_body_content_truncated>"
        }
        return body
    }

    private fun getNativeSource(input: BufferedSource, isGzipped: Boolean): BufferedSource {
        return if (isGzipped) {
            val source = GzipSource(input)
            source.buffer()
        } else {
            input
        }
    }

    private fun getNativeSource(response: Response): BufferedSource {
        if (bodyGzipped(response.headers)) {
            val source = response.peekBody(maxContentLength).source()
            if (source.buffer.size < maxContentLength) {
                return getNativeSource(source, true)
            } else {
                log("gzip encoded response was too long")
            }
        }
        return response.body!!.source()
    }


    private fun log(msg: String) {
        println(msg)
    }
}
