package project.ui.variablewatcher

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import project.LocalDebugBoard
import ir.amirab.debugboard.core.plugin.watcher.VariableWatcher
import ir.amirab.debugboard.core.plugin.watcher.WatcherItem
import project.utils.Result
import project.utils.rememberSuspendedValue
import project.widgets.TreeView


@Composable
fun rememberVariableWatcher(): VariableWatcher {
    return LocalDebugBoard.current.variableWatcher
}

@Composable
fun VariableWatcherView() {
    val watcher = rememberVariableWatcher()
    val list by watcher.watcherItems.collectAsState(emptyList())
    LazyColumn(Modifier.padding(8.dp)) {
        items(list) {
            RenderTree(it)
        }
    }
}


@Composable
fun RenderTree(item: WatcherItem<*>) {
    val (isOpen, setOpen) = remember {
        mutableStateOf(false)
    }
    TreeView(
        isOpen = isOpen,
        content = {
            VariableInfo(
                Modifier
                    .clickable { setOpen(!isOpen) }
                    .padding(horizontal = 2.dp)
                    .padding(vertical = 4.dp),
                item
            )
        },
        subtreeContent = {
            SubtreeContent(item)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VariableInfo(
    modifier: Modifier = Modifier,
    item: WatcherItem<*>,
) {
    val shortInfo = rememberSuspendedValue(item) {
        item.shortInfo
    }
    TooltipArea(
        {
            shortInfo.onReady {
                Surface {
                    Text(
                        it,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }

    ) {
        Row(
            modifier
        ) {
            Text(
                item.name,
                color = MaterialTheme.colors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(":")
            Text(
                item.shortenType,
                color = MaterialTheme.colors.primary.copy(alpha = 0.5f)
            )
            shortInfo.onReady {
                Spacer(Modifier.width(4.dp))
                Text(
                    "${item.value}",
                    Modifier.weight(1f),
                    color = MaterialTheme.colors.secondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }.onLoading {
                LinearProgressIndicator(
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SubtreeContent(item: WatcherItem<*>) {
    val children: Result<List<WatcherItem<*>>> = rememberSuspendedValue(item) {
        item.children
    }
    var lastLoadedChildren: List<WatcherItem<*>> by remember(item.name) {
        mutableStateOf(emptyList())
    }
    LaunchedEffect(item.name, children.value) {
        children.onReady {
            lastLoadedChildren = it
        }
    }
    Column {
        lastLoadedChildren.forEach {
            key(it.name) {
                RenderTree(it)
            }
        }
        Divider()
    }
}


@Composable
fun DebagBoardView() {
    VariableWatcherView()
}
