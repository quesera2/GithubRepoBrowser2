package que.sera.sera.githubbrowser2

import kotlin.native.ObjCName

data class SearchViewState(
    val isLoading: Boolean = false,
    val user: GitHubUser? = null,
    val repos: List<GitHubRepo>? = null,
    val errorMessage: ErrorMessage<SearchViewError>? = null,
) {
    companion object {
        @OptIn(kotlin.experimental.ExperimentalObjCName::class)
        @ObjCName(swiftName = "initialState")
        @Suppress("unused") // Used from Swift
        val INITIAL_STATE = SearchViewState()
    }

    val isError: Boolean
        get() = errorMessage != null

    fun idle() = copy(
        isLoading = false,
        errorMessage = null,
    )

    fun loading() = copy(
        isLoading = true,
    )

    fun success(user: GitHubUser, repos: List<GitHubRepo>) = copy(
        isLoading = false,
        user = user,
        repos = repos,
        errorMessage = null
    )

    fun failure(errorMessage: ErrorMessage<SearchViewError>) = copy(
        isLoading = false,
        errorMessage = errorMessage,
    )
}
