package ir.amirab.debugboard.core.plugin.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URL

class NetworkMonitor : INetworkMonitor {
    override val records = MutableStateFlow<List<NetworkData>>(emptyList()).asStateFlow()
    override val updatedRecords = MutableSharedFlow<NetworkData>().asSharedFlow()
    override fun onNewRequest(tag: Any, request: Request) {}
    override fun onResponse(tag: Any, response: Response) {}
}