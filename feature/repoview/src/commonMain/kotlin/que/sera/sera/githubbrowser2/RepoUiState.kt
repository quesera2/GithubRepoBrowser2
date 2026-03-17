package que.sera.sera.githubbrowser2

sealed class RepoUiState {
    data object Idle : RepoUiState()
    data object Loading : RepoUiState()
    data class Success(val repos: List<GitHubRepo>) : RepoUiState()
    data class Error(val message: String) : RepoUiState()
}
