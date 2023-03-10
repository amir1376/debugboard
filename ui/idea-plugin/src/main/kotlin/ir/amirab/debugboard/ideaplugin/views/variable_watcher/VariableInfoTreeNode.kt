package ir.amirab.debugboard.ideaplugin.views.variable_watcher

import ir.amirab.debugboard.api.models.ApiVariableInfo
import java.util.*
import javax.swing.tree.TreeNode

class VariableInfoTreeNode(
    val variableInfo: ApiVariableInfo,
    private val myParentNode: TreeNode?,
) : TreeNode, VariableTreeNodeProvider() {
    override fun toString(): String {
        return "${variableInfo.name}:${variableInfo.type} ${variableInfo.value}"
    }

    override fun getParent(): TreeNode? {
        return myParentNode
    }

    override val provideChildrenNodes by lazy {
        variableInfo.children?.map {
            VariableInfoTreeNode(it, this)
        } ?: emptyList()
    }
}