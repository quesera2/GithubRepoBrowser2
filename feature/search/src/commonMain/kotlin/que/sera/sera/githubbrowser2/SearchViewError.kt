package que.sera.sera.githubbrowser2

sealed class SearchViewError {
    data object EmptyUsername : SearchViewError()
    data object NetworkError : SearchViewError()
    data object UnknownError : SearchViewError()
}
