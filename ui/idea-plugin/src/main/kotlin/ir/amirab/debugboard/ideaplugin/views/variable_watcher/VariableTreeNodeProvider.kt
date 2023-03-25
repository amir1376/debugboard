package ir.amirab.debugboard.ideaplugin.views.variable_watcher

import java.util.*
import javax.swing.tree.TreeNode

abstract class VariableTreeNodeProvider : TreeNode {
    abstract val provideChildrenNodes: List<VariableInfoTreeNode>

    override fun getChildAt(childIndex: Int): TreeNode {
        return provideChildrenNodes[childIndex]
    }

    override fun getChildCount(): Int {
        return provideChildrenNodes.size
    }

    override fun getParent(): TreeNode? {
        return null
    }

    override fun getIndex(node: TreeNode?): Int {
        return provideChildrenNodes.indexOfFirst {
            it == node
        }
    }

    override fun getAllowsChildren(): Boolean {
        return true
    }

    override fun isLeaf(): Boolean {
        return false
    }

    override fun children(): Enumeration<out TreeNode> {
        return Collections.enumeration(provideChildrenNodes)
    }
}