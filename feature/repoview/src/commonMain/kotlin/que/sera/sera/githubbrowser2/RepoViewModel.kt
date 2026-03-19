package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
class RepoViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    val state: StateFlow<RepoViewState>
        field = MutableStateFlow(RepoViewState())

    fun fetchRepos(username: String) {
        if (username.isEmpty()) {
            state.update { it.failure("ユーザー名が入力されていません") }
            return
        }

        viewModelScope.launch {
            state.update { it.loading() }
            try {
                val repos = repository.fetchRepos(username)
                state.update { it.success(repos) }
            } catch (e: Exception) {
                state.update { it.failure(e.message ?: "Unknown error") }
            }
        }
    }

    fun onErrorDismissed() {
        state.update { it.idle() }
    }
}