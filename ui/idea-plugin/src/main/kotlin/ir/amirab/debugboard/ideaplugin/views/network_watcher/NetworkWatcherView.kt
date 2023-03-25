package ir.amirab.debugboard.ideaplugin.views.network_watcher

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.jetbrains.rd.framework.util.withContext
import io.ktor.http.*
import ir.amirab.debugboard.api.models.ApiNetworkData
import ir.amirab.debugboard.ideaplugin.DebugBoardService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.swing.*

class NetworkWatcherView(
    private val service: DebugBoardService
) : OnePixelSplitter(false), Disposable {
    private val listPanel = ListSection()
    private val selectionPanel = SelectionPanel(service.project)
    private val scope = CoroutineScope(SupervisorJob())

    init {
        Disposer.register(this, listPanel)
        Disposer.register(this, selectionPanel)
        firstComponent = listPanel
        secondComponent = selectionPanel
        listPanel.selectedItemFlow.onEach {
            selectionPanel.onNewItemSelected(it)
        }.launchIn(scope)
        service.networkInfoFlow.onEach {
            onNewRecords(it)
        }.launchIn(scope)
    }

    override fun dispose() {
        scope.cancel()
    }

    fun onNewRecords(networkData: List<ApiNetworkData>) {
        listPanel.onNewData(networkData)
    }


}


