package ir.amirab.debugboard.ideaplugin.views.variable_watcher

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsUtil
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import javax.swing.JTree

class VariableInfoCellRenderer : ColoredTreeCellRenderer() {
    private val fieldColor = EditorColorsUtil.getGlobalOrDefaultColorScheme()
        .getAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD).foregroundColor
    private val valueColor = EditorColorsUtil.getGlobalOrDefaultColorScheme()
        .getAttributes(DefaultLanguageHighlighterColors.STRING).foregroundColor

    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        if (value !is VariableInfoTreeNode) return

        val variableInfo = value.variableInfo
        append(variableInfo.name, SimpleTextAttributes(null, fieldColor, null, 0))
        append(" : ")
        append(variableInfo.type, SimpleTextAttributes.GRAYED_ATTRIBUTES)
        append(" ")
        append(variableInfo.value, SimpleTextAttributes(null, valueColor, null, 0))
    }
}