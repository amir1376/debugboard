package project.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TreeView(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    content: @Composable () -> Unit,
    subtreeContent: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        content()
        AnimatedVisibility(
            isOpen,
        ) {
            Box {
                Box(Modifier.matchParentSize()) {
                    Spacer(
                        Modifier.fillMaxHeight()
                            .width(1.dp)
                            .background(LocalContentColor.current.copy(alpha = 0.2f))
                    )
                }
                Box(Modifier.offset(x = 8.dp)) { subtreeContent() }
            }
        }
    }
}

