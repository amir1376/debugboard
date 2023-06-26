package ir.amirab.debugboard.ideaplugin.views

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.JBTabbedPane
import ir.amirab.debugboard.ideaplugin.session.ConnectionStatus
import ir.amirab.debugboard.ideaplugin.actions.DebugBoardConfigConnectionToggle
import ir.amirab.debugboard.ideaplugin.session.Session
import ir.amirab.debugboard.ideaplugin.views.network_watcher.NetworkWatcherView
import ir.amirab.debugboard.ideaplugin.views.variable_watcher.VariableWatcherViewView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.BorderLayout
import javax.swing.JPanel

class DebugBoardPanel(
    private val session: Session,
) : JPanel(BorderLayout()), Disposable {
    lateinit var updateTitle: (String) -> Unit
    var title = session.name
    private val scope = CoroutineScope(SupervisorJob())
    fun init() {
        val group = DefaultActionGroup().apply {
            add(DebugBoardConfigConnectionToggle(session))
        }
        val actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, false)
        actionToolbar.targetComponent = this

        //left toolbar
        add(actionToolbar.component, BorderLayout.WEST)

        //tab pages
        add(JBTabbedPane().apply {
            addTab(
                "Variable Watcher",
                AllIcons.Debugger.Watch,
                VariableWatcherViewView(session).also {
                    Disposer.register(this@DebugBoardPanel, it)
                }
            )
            addTab(
                "Network Watcher",
                AllIcons.Actions.InlayGlobe,
                NetworkWatcherView(session).also {
                    Disposer.register(this@DebugBoardPanel, it)
                }
            )
        }, BorderLayout.CENTER)

        session.backendStatus().onEach {
            updateTitle("$title (${getTitleOfConnectionStatus(it)})")
        }.launchIn(scope)
    }

    private fun getTitleOfConnectionStatus(status: ConnectionStatus): String {
        return when (status) {
            is ConnectionStatus.Connected -> "Connected"
            is ConnectionStatus.Connecting -> "Connecting"
            ConnectionStatus.IDLE -> "Idle"
        }
    }

    override fun dispose() {
        scope.cancel()
    }
}