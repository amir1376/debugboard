package ir.amirab.debugboard.ideaplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.Messages
import io.ktor.http.*
import io.ktor.http.Url
import ir.amirab.debugboard.ideaplugin.DebugBoardService
import ir.amirab.debugboard.ideaplugin.session.Session

@Suppress("ComponentNotRegistered")
class DebugBoardConfigConnectionToggle(
    val session: Session
) : DumbAwareAction() {
    init {
        templatePresentation.apply {
            text = "Toggle Connection"
            description = "Toggle connect/disconnect"
        }
    }

    override fun update(e: AnActionEvent) {
        val project: Project = e.project ?: return

        val service = project.service<DebugBoardService>()
        if (session.isConnectedOrConnecting()) {
            e.presentation.icon = AllIcons.Actions.Suspend
        } else {
            e.presentation.icon = AllIcons.Actions.Execute
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val service = project.service<DebugBoardService>()
        if (session.isConnectedOrConnecting()) {
            session.disconnect()
        } else {
            val address = Messages.showInputDialog(
                e.project,
                "Enter address to connect to",
                "Enter Address",
                null,
                service.config.lastUsedHost,
                InputValidatorEx {
                    val url = Url(it)
                    when {
                        url.protocol !in listOf(URLProtocol.WS, URLProtocol.WSS) -> {
                            "Protocol must be ws or wss"
                        }

                        else -> null
                    }
                },
                null,
                "For example ws://localhost:8000",
            )
            address?.let {
                val addr = Url(address).toString()
                thisLogger().info("connecting... to $addr")
                session.connect(addr)
            }
        }
    }
}
