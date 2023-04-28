package ir.amirab.debugboard.core.plugin.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor:INetworkMonitor {
    private val _allRecords: MutableStateFlow<List<NetworkData>> = MutableStateFlow(emptyList())
    override val records = _allRecords.asStateFlow()
    private val _updatedRecords = MutableSharedFlow<NetworkData>(extraBufferCapacity = 64)
    override val updatedRecords = _updatedRecords.asSharedFlow()

    override fun onNewRequest(
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

    override fun onResponse(tag: Any, response: Response) {
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