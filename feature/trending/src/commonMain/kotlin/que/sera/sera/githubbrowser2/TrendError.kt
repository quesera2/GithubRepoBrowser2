package que.sera.sera.githubbrowser2

sealed class TrendError {
    data object NetworkError : TrendError()
    data object UnknownError : TrendError()
}
