package que.sera.sera.githubbrowser2

sealed class RepoError {
    data object EmptyUsername : RepoError()
    data object NetworkError : RepoError()
    data object UnknownError : RepoError()
}
