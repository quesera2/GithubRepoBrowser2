package que.sera.sera.githubbrowser2

import kotlin.native.ObjCName

data class TrendViewState(
    val isLoading: Boolean = false,
    val repos: List<GitHubRepo>? = null,
    val errorMessage: ErrorMessage? = null,
) {
    companion object {
        @OptIn(kotlin.experimental.ExperimentalObjCName::class)
        @ObjCName(swiftName = "initialState")
        @Suppress("unused") // Used from Swift
        val INITIAL_STATE = TrendViewState()
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

    fun success(repos: List<GitHubRepo>) = copy(
        isLoading = false,
        repos = repos,
        errorMessage = null
    )

    fun failure(errorMessage: ErrorMessage) = copy(
        isLoading = false,
        errorMessage = errorMessage,
    )
}
