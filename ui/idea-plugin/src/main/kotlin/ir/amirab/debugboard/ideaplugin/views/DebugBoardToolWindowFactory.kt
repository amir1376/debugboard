package ir.amirab.debugboard.ideaplugin.views

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import ir.amirab.debugboard.ideaplugin.DebugBoardService


class DebugBoardToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val debugBoardService = project.service<DebugBoardService>()
        val panel = DebugBoardPanel(debugBoardService)
        val content = contentManager.factory.createContent(panel, panel.title, false)
        panel.updateTitle = {
            content.displayName = it
        }
        content.setDisposer(panel)
        panel.init()
        contentManager.addContent(content)
    }
}

