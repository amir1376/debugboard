package ir.amirab.debugboard.plugin.network.okhttp

import okhttp3.*
import ir.amirab.debugboard.core.DebugBoard


class OkHttpDebugBoardInterceptor(
    private val debugBoard: DebugBoard = DebugBoard.Default
) : Interceptor {
    fun maxContentLength(max: Long): OkHttpDebugBoardInterceptor {
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}

