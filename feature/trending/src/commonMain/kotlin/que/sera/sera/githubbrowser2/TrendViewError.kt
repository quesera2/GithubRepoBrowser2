package que.sera.sera.githubbrowser2

sealed class TrendViewError {
    data object NetworkError : TrendViewError()
    data object UnknownError : TrendViewError()
}
