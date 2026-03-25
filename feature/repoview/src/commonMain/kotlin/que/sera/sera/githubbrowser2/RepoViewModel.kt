package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import que.sera.sera.githubbrowser2.MR

@Inject
class RepoViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    val state: StateFlow<RepoViewState>
        field = MutableStateFlow(RepoViewState())

    fun fetchRepos(username: String) {
        if (username.isEmpty()) {
            state.update {
                it.failure(ErrorMessage.CancelOnly(MR.strings.please_enter_username.desc()))
            }
            return
        }

        viewModelScope.launch {
            state.update { it.loading() }
            try {
                val repos = repository.fetchRepos(username)
                state.update { it.success(repos) }
            } catch (e: Exception) {
                val message = e.message?.desc() ?: MR.strings.unknown_error.desc()
                state.update {
                    it.failure(
                        ErrorMessage.CanRetry(
                            message = message,
                            retryAction = { fetchRepos(username) })
                    )
                }
            }
        }
    }

    fun onErrorDismissed() {
        state.update { it.idle() }
    }
}