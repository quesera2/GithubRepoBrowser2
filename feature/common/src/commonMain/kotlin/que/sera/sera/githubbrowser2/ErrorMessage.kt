package que.sera.sera.githubbrowser2

sealed class ErrorMessage<out T> {
    abstract val error: T

    data class CancelOnly<T>(override val error: T) : ErrorMessage<T>()

    class CanRetry<T>(
        override val error: T,
        val retryAction: () -> Unit,
    ) : ErrorMessage<T>()
}
