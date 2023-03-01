package project.ui.network

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ir.amirab.debugboard.core.plugin.network.FailResponse
import ir.amirab.debugboard.core.plugin.network.NetworkData
import ir.amirab.debugboard.core.plugin.network.SuccessResponse
import project.widgets.Expandable

enum class RecordInfoSection {
    Request, Response
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RecordInfo(currentRecord: NetworkData) {
    val (selectedSection, setSelectedSection) = remember(currentRecord.tag) {
        mutableStateOf(RecordInfoSection.Request)
    }
    Column(
    ) {
        NetworkRecordRow(Modifier.fillMaxWidth(), currentRecord)
        Spacer(Modifier.size(8.dp))
        SectionSelection(selectedSection, setSelectedSection)
        Spacer(Modifier.size(8.dp))
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            AnimatedContent(selectedSection) {
                when (it) {
                    RecordInfoSection.Request -> {
                        RequestSection(currentRecord)
                    }

                    RecordInfoSection.Response -> {
                        ResponseSection(currentRecord)
                    }
                }
            }
        }
    }
}

@Composable
fun TitleWithValueView(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    value: (@Composable () -> Unit)? = null
) {
    Row(modifier = modifier) {
        Box(
            Modifier
                .weight(value?.let { 0.4f } ?: 1f),
        ) {
            CompositionLocalProvider(
                LocalContentAlpha provides 0.6f
            ) {
                title()
            }
        }
        value?.let {
            Box(Modifier.weight(1f)) {
                CompositionLocalProvider(
                    LocalContentAlpha provides 1f
                ) {
                    it()
                }
            }
        }

    }
}

@Composable
fun HeaderItemWithValue(
    modifier: Modifier = Modifier,
    name: @Composable () -> Unit,
    value: @Composable () -> Unit
) {
    Row(modifier = modifier) {
        Box(
            Modifier
                .weight(0.4f)
        ) {
            CompositionLocalProvider(
                LocalContentAlpha provides 0.7f
            ) {
                name()
            }
        }
        Box(Modifier.weight(0.6f)) {
            CompositionLocalProvider(
                LocalContentAlpha provides 1f
            ) {
                value()
            }
        }
    }
}


@Composable
private fun RenderHeaders(headers: Map<String, List<String>>) {
    val (isOpen, setOpen) = remember {
        mutableStateOf(true)
    }
    Expandable(
        Modifier.padding(horizontal = 8.dp),
        isOpen = isOpen,
        content = {
            Row(
                Modifier.clickable {
                    setOpen(!isOpen)
                }.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                TitleWithValueView(
                    modifier = Modifier.weight(1f),
                    title = {
                        CompositionLocalProvider(
                            LocalContentAlpha provides 1f
                        ) {
                            Text("Headers")
                        }
                    }
                )
                ExpandIcon(isOpen)
            }
        },
        subtreeContent = {
            Column {
                headers.forEach {
                    HeaderItemWithValue(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        name = {
                            Text(it.key)
                        },
                        value = {
                            Text(it.value.joinToString("; "))
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun RenderBody(body: Any?) {
    @Composable
    fun RowScope.BodyTitle(value: (@Composable () -> Unit)? = null) {
        TitleWithValueView(
            modifier = Modifier.weight(1f),
            title = {
                CompositionLocalProvider(
                    LocalContentAlpha provides 1f
                ) {
                    Text("Body")
                }
            },
            value = value
        )
    }
    when (body) {
        is String -> {
            val (isOpen, setOpen) = remember {
                mutableStateOf(true)
            }
            Expandable(
                Modifier.padding(horizontal = 8.dp),
                isOpen = isOpen,
                content = {
                    Row(
                        Modifier.clickable {
                            setOpen(!isOpen)
                        }.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        BodyTitle()
                        ExpandIcon(isOpen)
                    }
                },
                subtreeContent = {
                    Column {
                        Text(
                            body.toString(),
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(5))
                                .background(LocalContentColor.current.copy(alpha = 0.1f))
                                .padding(8.dp),
                        )
                    }
                }
            )
        }

        null -> {
            Row {
                BodyTitle {
                    Text("<empty>")
                }
            }
        }

        else -> {
            Row {
                BodyTitle {
                    Text("<${body::class.simpleName} not supported>")
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandIcon(open: Boolean) {
    AnimatedContent(open) {
        if (it) {
            Icon(Icons.Rounded.ExpandMore, "more")
        } else {
            Icon(Icons.Rounded.ExpandLess, "less")
        }
    }
}


@Composable
private fun RequestSection(currentRecord: NetworkData) {
    Column {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))
            TitleWithValueView(
                Modifier.fillMaxWidth(),
                title = {
                    Text("URL")
                },
                value = {
                    Text(currentRecord.request.url)
                },
            )
            Spacer(Modifier.height(16.dp))
            TitleWithValueView(
                Modifier.fillMaxWidth(),
                title = {
                    Text("Method")
                },
                value = {
                    Text(currentRecord.request.method)
                },
            )
            Spacer(Modifier.height(16.dp))
        }
        RenderHeaders(currentRecord.request.headers)
        RenderBody(currentRecord.request.body)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ResponseSection(currentRecord: NetworkData) {
    AnimatedContent(currentRecord.response) {
        when (it) {
            is FailResponse -> {
                Column {
                    Text(
                        "error happened", Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(vertical = 64.dp)
                    )
                }
            }

            is SuccessResponse -> {
                Column {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Spacer(Modifier.height(16.dp))
                        TitleWithValueView(
                            Modifier.fillMaxWidth(),
                            title = {
                                Text("Status Code")
                            },
                            value = {
                                Text("${it.code}")
                            },
                        )
                        Spacer(Modifier.height(16.dp))
                        TitleWithValueView(
                            Modifier.fillMaxWidth(),
                            title = {
                                Text("Description")
                            },
                            value = {
                                Text(it.description)
                            },
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    RenderHeaders(it.headers)
                    RenderBody(it.body)
                }
            }

            null -> {
                Column {
                    Text(
                        "no data yet", Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(vertical = 64.dp)
                    )
                    CircularProgressIndicator()
                }
            }
        }

    }
}

@Composable
fun SectionSelection(
    selected: RecordInfoSection,
    setSelectedSection: (RecordInfoSection) -> Unit
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Row {
            listOf(
                RecordInfoSection.Request,
                RecordInfoSection.Response,
            ).forEach {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            setSelectedSection(it)
                        },
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(it.name, Modifier.padding(8.dp))
                    if (it == selected) {
                        Spacer(
                            Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.primary)
                        )
                    }
                }
            }
        }
        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(LocalContentColor.current.copy(0.1f))
        )
    }
}
