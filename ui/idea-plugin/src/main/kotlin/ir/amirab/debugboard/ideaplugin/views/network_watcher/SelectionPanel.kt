package ir.amirab.debugboard.ideaplugin.views.network_watcher

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.*
import ir.amirab.debugboard.api.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.datatransfer.StringSelection
import javax.swing.*

class SelectionPanel(
    private val project: Project
) : JPanel(BorderLayout()), ComponentWithEmptyText, Disposable {

    override fun getComponentGraphics(graphics: Graphics?): Graphics? {
        return JBSwingUtilities.runGlobalCGTransform(this, super.getComponentGraphics(graphics))
    }

    override fun getPreferredSize(): Dimension {
        val s = emptyText.preferredSize
        JBInsets.addTo(s, insets)
        val size = super.getPreferredSize()
        return Dimension(
            Math.max(s.width, size.width),
            Math.max(s.height, size.height)
        )
    }

    private var currentData: ApiNetworkData? = null
    private val myEmptyText: StatusText = object : StatusText(this) {
        override fun isStatusVisible(): Boolean {
            return currentData == null
        }
    }

    init {
        myEmptyText.text = "Please select a record network first."
        updateUi()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        myEmptyText.paint(this, g)
    }

    fun onNewItemSelected(newData: ApiNetworkData?) {
        val currentData = currentData
        this.currentData = newData
        if (currentData == null) {
            if (newData != null) {
                updateUi()
            }
        } else {
            if (newData == null) {
                updateUi()
            } else {
                if (newData.tag == currentData.tag) {
                    if (newData != currentData) {
                        updateUi()
                    }
                } else {
                    updateUi()
                }
            }
        }
    }

    private fun updateUi() {
        SwingUtilities.invokeLater {
            this.removeAll()
            val data = currentData
            if (data != null) {
                add(createNetworkViewPanel(data), BorderLayout.CENTER)
            }
            revalidate()
        }
    }


    private fun createNetworkViewPanel(data: ApiNetworkData): JComponent {
        return JBTabbedPane().apply {
            addTab(
                "Request",
                JBScrollPane(
                    createRequestSection(data.request),
                ).apply {
                    horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                },
            )
            addTab(
                "Response",
                JBScrollPane(
                    createResponseSection(data.response)
                ).apply {
                    horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                },
            )
        }
    }

    private fun createRequestSection(request: ApiRequest): Component {
        return panel {
            row("Url") {
                textWithCopy(request.url)
            }
            row("Method") {
                textWithCopy(request.method)
            }
            renderHeaders(request.headers)
            renderBody(request.body, getTypeFromHeaders(request.headers))
        }
    }

    private fun Panel.renderBody(body: String?, typeFromHeaders: String) {
        collapsibleGroup("Body") {
            when (body) {
                "" -> {
                    row {
                        label("Body is empty")
                    }
                }

                null -> {
                    row {
                        label("Body is null")
                    }
                }

                else -> {
                    row {
                        val stdFileType = FileTypeManager.getInstance().getFileTypeByExtension(typeFromHeaders)
                        cell(EditorTextField(
                            body,
                            project,
                            stdFileType,
                        ).apply {
                            setViewer(true)
                            setOneLineMode(false)
                            val defaultBackgroundColor =
                                EditorColorsManager.getInstance().globalScheme.defaultBackground
                            UIUtil.setBackgroundRecursively(this, defaultBackgroundColor)
                            setPreferredWidth(Int.MAX_VALUE)
                        })
                    }
                }
            }
        }.apply {
            expanded = true
        }
    }

    private fun Panel.renderHeaders(headers: Map<String, List<String>>) {
        collapsibleGroup("Headers (${headers.size})") {
            headers.forEach {
                row(it.key) {
                    val valueOfHeader = it.value.joinToString("; ")
                    textWithCopy(valueOfHeader)
                }
            }
        }.apply {
            expanded = true
        }
    }

    fun <T> MutableStateFlow<T>.update(block: (T) -> T) {
        value = block(value)
    }

    private fun createResponseSection(response: ApiNetworkResponse?): Component {
        return when (response) {
            is ApiFailResponse -> {
                JPanel(BorderLayout()).apply {
                    add(JLabel(response.cause))
                }
            }

            is ApiSuccessResponse -> {
                panel {
                    row {
                        label("Code")
                        textWithCopy(response.code.toString())
                    }
                    row {
                        label("Description")
                        textWithCopy(response.description)
                    }
                    renderHeaders(response.headers)
                    renderBody(response.body, getTypeFromHeaders(response.headers))
                }
            }

            null -> {
                JPanel(BorderLayout()).apply {
                    add(JLabel("No response yet."))
                }
            }
        }
    }

    private fun getTypeFromHeaders(headers: Map<String, List<String>>): String {
        var type = ""
        for (header in headers) {
            if (header.key.lowercase() == "content-type") {
                header.value
                    .firstOrNull()
                    ?.let {
                        type = it
                    }
                break
            }
        }
        val t = type.lowercase()
        return when {
            t.contains("json") -> "json"
            t.contains("html") -> "html"
            t.contains("xml") -> "xml"
            else -> "txt"
        }
    }


    override fun getEmptyText(): StatusText {
        return myEmptyText
    }

    override fun dispose() {
    }

}

private fun Row.textWithCopy(value: String) {
    text(value)
        .applyToComponent {
            isFocusable = true
        }
        .resizableColumn()
    actionButton(CopyTextAction(value))
}

private class CopyTextAction(private val valueOfHeader: String) : DumbAwareAction() {
    init {
        this.templatePresentation.icon = AllIcons.Actions.Copy
    }

    override fun actionPerformed(e: AnActionEvent) {
        CopyPasteManager.getInstance().setContents(StringSelection(valueOfHeader))
    }
}
