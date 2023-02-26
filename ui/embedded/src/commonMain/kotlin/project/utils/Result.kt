package project.utils


@Suppress("UNCHECKED_CAST")
class Result<T>(
    val value: Any?
) {
    private object Error
    private object Loading
    data class Success(val data: Any?)
    companion object {
        fun <T> loading() = Result<T>(Loading)
        fun <T> error() = Result<T>(Error)
        fun <T> success(value: T) = Result<T>(Success(value))
    }

    fun isError() = value is Error
    fun isLoading() = value is Loading
    fun isSuccess() = value is Success

    inline fun onError(block: () -> Unit) = apply {
        if (isError()) {
            block()
        }
    }

    inline fun onLoading(block: () -> Unit) = apply {
        if (isLoading()) {
            block()
        }
    }

    inline fun onReady(block: (T) -> Unit) = apply {
        if (isSuccess()) {
            block((value as Success).data as T)
        }
    }

    fun getOrElse(elseBlock: () -> T): T {
        return if (isSuccess()) {
            (value as Success).data as T
        } else {
            elseBlock()
        }
    }

}