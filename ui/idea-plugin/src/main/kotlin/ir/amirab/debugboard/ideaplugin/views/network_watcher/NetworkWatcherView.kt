package ir.amirab.debugboard.ideaplugin.views.network_watcher

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.ui.OnePixelSplitter
import io.ktor.http.*
import ir.amirab.debugboard.api.models.ApiNetworkData
import ir.amirab.debugboard.ideaplugin.session.Session
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.swing.*

class NetworkWatcherView(
    private val session: Session
) : OnePixelSplitter(false), Disposable {
    private val listPanel = ListSection()
    private val selectionPanel = SelectionPanel(session.project)
    private val scope = CoroutineScope(SupervisorJob())

    init {
        Disposer.register(this, listPanel)
        Disposer.register(this, selectionPanel)
        firstComponent = listPanel
        secondComponent = selectionPanel
        listPanel.selectedItemFlow.onEach {
            selectionPanel.onNewItemSelected(it)
        }.launchIn(scope)
        session.networkInfoFlow.onEach {
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


