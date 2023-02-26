package project.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Expandable(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    content: @Composable () -> Unit,
    subtreeContent: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        content()
        AnimatedVisibility(
            isOpen,
            Modifier.padding(4.dp)
        ) {
            subtreeContent()
        }
    }
}