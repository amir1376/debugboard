package ir.amirab.debugboard.ideaplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import ir.amirab.debugboard.ideaplugin.DebugBoardService

class NewSessionAction : DumbAwareAction() {

    init {
        templatePresentation.apply {
            text = "New Session"
            description = "Add new Debug Board session"
            icon = AllIcons.General.Add
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.service<DebugBoardService>()?.addSession()
    }
}