package ir.amirab.debugboard.core.plugins

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

class NetworkMonitor {
    private val _allRecords: MutableStateFlow<List<NetworkData>> = MutableStateFlow(emptyList())
    val records = _allRecords.asStateFlow()
    private val _updatedRecords = MutableSharedFlow<NetworkData>(extraBufferCapacity = 64)
    val updatedRecords = _updatedRecords.asSharedFlow()

    fun onNewRequest(
        tag: Any,
        request: Request,
    ) {
        val dataList = _allRecords.value.toMutableList()
        val foundIndex = dataList.indexOfFirst {
            it.tag == tag
        }
        val nData = NetworkData(
            tag = tag,
            request = request,
        )
        if (foundIndex == -1) {
            dataList.add(nData)
        } else {
            dataList[foundIndex] = nData
        }
        _allRecords.value = dataList.toList()
        _updatedRecords.tryEmit(nData)
    }

    fun onResponse(tag: Any, response: Response) {
        val dataList = _allRecords.value.toMutableList()
        val foundIndex = dataList.indexOfFirst {
            it.tag == tag
        }
        val nData = dataList[foundIndex].copy(response = response)
        dataList[foundIndex] = nData
        _allRecords.value = dataList.toList()
        _updatedRecords.tryEmit(nData)
    }
}