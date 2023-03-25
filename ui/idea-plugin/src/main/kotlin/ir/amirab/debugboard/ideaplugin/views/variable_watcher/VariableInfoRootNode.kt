package ir.amirab.debugboard.ideaplugin.views.variable_watcher

import ir.amirab.debugboard.api.models.ApiVariableInfo
import java.util.*
import javax.swing.tree.TreeNode

class VariableInfoRootNode : TreeNode, VariableTreeNodeProvider() {
    private var children = emptyList<ApiVariableInfo>()
    override val provideChildrenNodes: List<VariableInfoTreeNode>
        get() = childrenNodes
    lateinit var childrenNodes: List<VariableInfoTreeNode>

    init {
        refreshChildTree()
    }

    override fun getParent(): TreeNode? {
        return null
    }


    fun setVariableInfo(variableInfoList: List<ApiVariableInfo>) {
        children = variableInfoList
        refreshChildTree()
    }

    private fun refreshChildTree() {
        childrenNodes = children.map {
            VariableInfoTreeNode(it, this)
        }
    }
}