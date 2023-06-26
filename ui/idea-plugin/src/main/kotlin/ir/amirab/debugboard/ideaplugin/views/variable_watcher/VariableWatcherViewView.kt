package ir.amirab.debugboard.ideaplugin.views.variable_watcher

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeLater
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import ir.amirab.debugboard.api.models.ApiVariableInfo
import ir.amirab.debugboard.ideaplugin.session.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath


class VariableWatcherViewView(
    private val session: Session
) : JPanel(GridLayout()), Disposable {
    val scope = CoroutineScope(SupervisorJob())

    private val rootNode = VariableInfoRootNode()


    private val treeModel = DefaultTreeModel(rootNode)
    private val expandedPaths = mutableSetOf<List<String>>()

    private val treeExpansionListener = object : TreeExpansionListener {
        override fun treeExpanded(event: TreeExpansionEvent?) {
            event ?: return
            val path = event.path.toStringPath()
            if (expandedPaths.add(path)) {
                session.setOpenedPaths(expandedPaths)
            }
        }

        override fun treeCollapsed(event: TreeExpansionEvent?) {
            event ?: return
            expandedPaths.remove(event.path.toStringPath())
        }

    }
    private val tree: Tree = Tree(treeModel).apply {
        isRootVisible = false
        cellRenderer = VariableInfoCellRenderer()
        addTreeExpansionListener(treeExpansionListener)
    }

    // I call this method somewhere else and I want
    // Tree to update itself and does not lose selection and expansion state


    fun onVariablesUpdate(list: List<ApiVariableInfo>) {
        invokeLater {
            rootNode.setVariableInfo(list)
            val oldPaths = tree.selectionPaths.orEmpty().map {
                it.toStringPath()
            }
            treeModel.nodeStructureChanged(rootNode)
            convertStringPathsToTreePaths(expandedPaths).forEach {
                tree.expandPath(it)
            }
            tree.selectionPaths = convertStringPathsToTreePaths(oldPaths)
        }
    }

    fun createTreePath(path: List<String>): TreePath? {
        val segments = mutableListOf<TreeNode>(rootNode)
        var currentNode: VariableTreeNodeProvider = rootNode
        for (s in path) {
            val find = currentNode.provideChildrenNodes.find {
                it.variableInfo.name == s
            }
            if (find != null) {
                currentNode = find
                segments.add(find)
            } else {
                return null
            }
        }
        return TreePath(segments.toTypedArray())
    }

    private fun convertStringPathsToTreePaths(oldPaths: Iterable<List<String>>): Array<TreePath> {
        return oldPaths.mapNotNull {
            createTreePath(it)
        }.toTypedArray()
    }


    init {
        add(JBScrollPane(tree))
        session.variableInfoFlow.onEach {
            onVariablesUpdate(it)
        }.launchIn(scope)
    }


    override fun dispose() {
        tree.removeTreeExpansionListener(treeExpansionListener)
        scope.cancel()
    }
}

private fun TreePath.toStringPath(): List<String> {
    return path.mapNotNull {
        when (it) {
            is VariableInfoTreeNode -> it.variableInfo.name
            else -> null
        }
    }
}