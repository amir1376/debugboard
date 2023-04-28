package project.ui.network

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.amirab.debugboard.core.plugin.network.*
import project.LocalDebugBoard

@Composable
fun rememberNetworkMonitor(): NetworkMonitor {
    return LocalDebugBoard.current.networkMonitor
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NetworkView() {
    val monitor = rememberNetworkMonitor()
    val records by monitor.records.collectAsState()
    var currentRecordTag by remember {
        mutableStateOf<Any?>(null)
    }
    val currentRecord = remember(records, currentRecordTag) {
        currentRecordTag.let { tag ->
            records.find {
                it.tag === tag
            }
        }?.also {
            println("$it")
        }
    }

    AnimatedContent(currentRecord) { networkData ->
        if (networkData == null) {
            RecordList(records) { clickedOnThisData ->
                currentRecordTag = clickedOnThisData.tag
            }
        } else {
            RecordInfo(networkData)
        }
    }
}


@Composable
fun RecordList(
    records: List<NetworkData>,
    onRecordClick: (NetworkData) -> Unit
) {
    LazyColumn {
        itemsIndexed(records) { index, item ->
            if (index != 0) {
                Spacer(Modifier.size(8.dp))
            }
            NetworkRecordRow(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onRecordClick(item)
                    }, item
            )
        }
    }
}

@Composable
fun NetworkRecordRow(
    modifier: Modifier,
    networkData: NetworkData
) {
    val url = remember(networkData.request.url) {
        networkData.request.javaUrl
    }
    Column(
        modifier
            .background(LocalContentColor.current.copy(alpha = 0.1f))
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RenderStatus(networkData.response)
            Spacer(Modifier.size(8.dp))
            RenderMethod(networkData.request.method)
            Spacer(Modifier.size(8.dp))
            RenderDomain(url.host)
        }
        Spacer(Modifier.size(8.dp))
        RenderPath(url.path)
    }
}

@Composable
fun RenderPath(path: String) {
    Text(path)
}

@Composable
fun RenderDomain(host: String) {
    Text(host, overflow = TextOverflow.Ellipsis)
}

@Composable
fun RenderMethod(
    method: String
) {
    Text(method, fontWeight = FontWeight.Bold)
}

@Composable
fun RenderStatus(response: Response?) {
    Box(
        Modifier.widthIn(min = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        val iconSize = Modifier.size(20.dp)
        when (response) {
            is FailResponse -> {
                Icon(
                    Icons.Filled.Error, "error",
                    modifier = iconSize,
                    tint = MaterialTheme.colors.error
                )
            }

            is SuccessResponse -> {
                HaveResponseWidget(response.code)
            }

            null -> {
                CircularProgressIndicator(iconSize)
            }
        }
    }
}

@Composable
fun HaveResponseWidget(code: Int) {
    val color = when (code) {
        in 200..299 -> Color(0xff55ff55)
        in 300..599,
        -> Color(0xff55ff55)

        else -> LocalContentColor.current
    }
    Text(
        "$code", color = color, modifier = Modifier.border(
            1.dp, color, RoundedCornerShape(5)
        ).padding(2.dp)
    )
}