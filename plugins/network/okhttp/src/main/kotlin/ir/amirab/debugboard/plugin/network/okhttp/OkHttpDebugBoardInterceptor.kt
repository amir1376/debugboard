package ir.amirab.debugboard.plugin.network.okhttp

import okhttp3.*
import ir.amirab.debugboard.core.DebugBoard


class OkHttpDebugBoardInterceptor(
    private val debugBoard: DebugBoard = DebugBoard.Default
) : Interceptor {
    private var maxContentLength = 250000L

    fun maxContentLength(max: Long): OkHttpDebugBoardInterceptor {
        maxContentLength = max
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val processor = OkhttpRequestResponseProcessor(
            debugBoard, maxContentLength,
        )
        processor.processRequest(request)
        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            processor.processResponse(e)
            throw e
        }
        processor.processResponse(response)
        return response
    }
}

