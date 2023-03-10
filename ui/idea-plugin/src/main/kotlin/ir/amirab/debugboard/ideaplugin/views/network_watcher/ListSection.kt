package ir.amirab.debugboard.ideaplugin.views.network_watcher

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeLater
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.UIUtil
import com.jgoodies.binding.adapter.AbstractTableAdapter
import com.jgoodies.binding.adapter.Bindings
import com.jgoodies.binding.list.SelectionInList
import com.jgoodies.binding.value.BindingConverter
import com.jgoodies.binding.value.ConverterValueModel
import com.jgoodies.binding.value.ValueHolder
import io.ktor.http.*
import ir.amirab.debugboard.api.models.ApiFailResponse
import ir.amirab.debugboard.api.models.ApiNetworkData
import ir.amirab.debugboard.api.models.ApiSuccessResponse
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class ListSection : JPanel(GridLayout(1, 1)), Disposable {
    var currentList = emptyList<ApiNetworkData>()

    /**
     * set and git will be [ApiNetworkData]
     **/
    val selectedItem = ConverterValueModel(
        ValueHolder(null),
        object : BindingConverter<String?, ApiNetworkData?> {
            override fun targetValue(sourceValue: String?): ApiNetworkData? {
                return currentList.find {
                    sourceValue == it.tag
                }
            }

            override fun sourceValue(targetValue: ApiNetworkData?): String? {
                return targetValue?.tag
            }
        }
    )

    private val model: SelectionInList<ApiNetworkData> = SelectionInList<ApiNetworkData>(
        emptyList(),
        selectedItem
    )

    val selectedItemFlow = MutableStateFlow(null as ApiNetworkData?)
    private val listPanel = JBTable(RecordTableModel())

    init {
        Bindings.bind(listPanel, model)
        model.selectionHolder.addValueChangeListener {
            val newData = it.newValue as? ApiNetworkData?
            selectedItemFlow.value = newData
        }
        add(JBScrollPane(listPanel))
    }


    fun onNewData(
        networkData: List<ApiNetworkData>,
    ) {
        invokeLater {
            currentList = networkData
            model.list = networkData
        }
    }

    override fun dispose() {
        model.release()
    }
}

private fun createCell(it: ApiNetworkData): JComponent {
    val url = Url(it.request.url)
    val statusLabel = JBLabel()
    val hostLabel = JBLabel(url.host)
    when (val r = it.response) {
        is ApiFailResponse -> {
            statusLabel
                .withBorder(BorderFactory.createDashedBorder(UIUtil.getErrorForeground()))
                .apply {
                    text = "!!!"
                }
        }

        is ApiSuccessResponse -> {
            statusLabel
                .withBorder(
                    BorderFactory.createDashedBorder(
                        when (r.code) {
                            in 200..299 -> JBColor(Color(38, 158, 63), Color(43, 182, 72))
                            in 300..599 -> UIUtil.getErrorForeground()
                            else -> UIUtil.getLabelForeground()
                        }
                    )
                )
                .apply {
                    text = r.code.toString()
                }
        }

        null -> {
            statusLabel.withBorder(BorderFactory.createDashedBorder(UIUtil.getLabelDisabledForeground()))
            statusLabel.text = "..."
        }
    }

    return JPanel(GridLayout(2, 1)).apply {
        alignmentX = Component.LEFT_ALIGNMENT;
        add(Box.createHorizontalBox().apply {
            isOpaque = false
            add(statusLabel)
            add(JPanel().apply {
                isOpaque = false
                preferredSize = Dimension(2, 0)
            })
            add(hostLabel)
        })
        add(JLabel(url.encodedPath))
    }
}

class RecordTableModel : AbstractTableAdapter<ApiNetworkData>(
    "Status", "Host", "Path",
) {
    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val element = this.listModel.getElementAt(rowIndex) as ApiNetworkData
        val url = Url(element.request.url)
        return when (columnIndex) {
            0 -> kotlin.run {
                when (val response = element.response) {
                    is ApiFailResponse -> "!!!"
                    is ApiSuccessResponse -> response.code
                    null -> "..."
                }
            }

            1 -> {
                url.host
            }

            2 -> {
                url.encodedPath
            }

            else -> "unknown column!"
        }
    }

}