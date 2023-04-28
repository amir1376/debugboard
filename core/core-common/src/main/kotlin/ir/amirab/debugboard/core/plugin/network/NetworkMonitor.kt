package ir.amirab.debugboard.core.plugin.network

import kotlinx.coroutines.flow.*
import java.net.URL

data class Request(
    val headers: Map<String, List<String>>,
    val method: String,
    val url: String,
    val body: Any?,
) {
    val javaUrl by lazy {
        URL(url)
    }
}

sealed interface Response
data class FailResponse(
    val cause: Exception
) : Response

data class SuccessResponse(
    val code: Int,
    val description: String,
    val headers: Map<String, List<String>>,
    val body: Any?,
) : Response


data class NetworkData(
    val tag: Any,
    val request: Request,
    val response: Response? = null,
)

interface  INetworkMonitor {
    val records :StateFlow<List<NetworkData>>
    val updatedRecords :SharedFlow<NetworkData>

    fun onNewRequest(
        tag: Any,
        request: Request,
    )

    fun onResponse(tag: Any, response: Response)
}