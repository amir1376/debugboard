package project.utils

import androidx.compose.runtime.*

@Composable
fun <T> rememberSuspendedValue(
    vararg keys: Any?,
    getter: suspend () -> T
): Result<T> {
    var result by remember(*keys) {
        mutableStateOf(Result.loading<T>())
    }
    LaunchedEffect(*keys) {
        result = Result.loading()
        kotlin.runCatching {
            getter()
        }.onFailure {
            result = Result.error()
        }.onSuccess {
            result = Result.success(it)
        }
    }
    return result
}