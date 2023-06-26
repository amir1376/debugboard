package ir.amirab.debugboard.ideaplugin.views

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentManager
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import ir.amirab.debugboard.ideaplugin.DebugBoardService
import ir.amirab.debugboard.ideaplugin.actions.NewSessionAction
import ir.amirab.debugboard.ideaplugin.session.Session
import ir.amirab.debugboard.ideaplugin.session.SessionListStatusEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class DebugBoardToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val debugBoardService = project.service<DebugBoardService>()
        //clean session resource on remove tab
        toolWindow.addContentManagerListener(object : ContentManagerListener {
            override fun contentRemoved(event: ContentManagerEvent) {
                event.content.getUserData(SessionKey)?.let {
                    debugBoardService.removeSession(it)
                }
            }
        })
        //add already available sessions
        for (s in debugBoardService.sessions) {
            addContent(contentManager, s)
        }
        //update future session add or remove
        debugBoardService.sessionUpdateFlow.onEach {
            try {
                when (it) {
                    is SessionListStatusEvent.AddSession -> {
                        addContent(contentManager, it.session)
                    }

                    is SessionListStatusEvent.RemoveSession -> removeContent(contentManager, it.session)
                }
            } catch (e: Exception) {
                thisLogger().error(e)
            }
        }
            .launchIn(debugBoardService.scope)
        (toolWindow as ToolWindowEx).setTabActions(
            NewSessionAction()
        )
    }

    private fun removeContent(contentManager: ContentManager, session: Session) {
        invokeLater {
            val content = contentManager.contents.find {
                it.getUserData(SessionKey) == session
            }
            if (content != null) {
                contentManager.removeContent(content, true)
            }
        }
    }

    private fun addContent(contentManager: ContentManager, session: Session) {
        invokeLater {
            val panel = DebugBoardPanel(session)
            val content = contentManager.factory.createContent(panel, panel.title, false)
            content.putUserData(SessionKey, session)
            panel.updateTitle = {
                content.displayName = it
            }
            content.setDisposer(panel)
            panel.init()
            contentManager.addContent(content)
        }
    }


}

private object SessionKey : Key<Session>("session_key")