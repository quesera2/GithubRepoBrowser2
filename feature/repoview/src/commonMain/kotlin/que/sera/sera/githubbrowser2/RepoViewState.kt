package que.sera.sera.githubbrowser2

import kotlin.native.ObjCName

data class RepoViewState(
    val isLoading: Boolean = false,
    val repos: List<GitHubRepo>? = null,
    val errorMessage: String = "",
) {
    companion object {
        @OptIn(kotlin.experimental.ExperimentalObjCName::class)
        @ObjCName(swiftName = "initialState")
        @Suppress("unused") // Used from Swift
        val INITIAL_STATE = RepoViewState()
    }

    val isError: Boolean
        get() = errorMessage.isNotEmpty()

    fun idle() = copy(
        isLoading = false,
        errorMessage = "",
    )

    fun loading() = copy(
        isLoading = true,
    )

    fun success(repos: List<GitHubRepo>) = copy(
        isLoading = false,
        repos = repos,
        errorMessage = ""
    )

    fun failure(errorMessage: String) = copy(
        isLoading = false,
        errorMessage = errorMessage,
    )
}
