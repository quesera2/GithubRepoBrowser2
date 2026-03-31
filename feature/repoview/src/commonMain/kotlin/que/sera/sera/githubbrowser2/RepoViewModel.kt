package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
            state.update {
                it.failure(ErrorMessage.CancelOnly(RepoError.EmptyUsername))
            }
            return
        }

        viewModelScope.launch {
            state.update { it.loading() }
            try {
                coroutineScope {
                    val user = async { repository.fetchUser(username) }
                    val repos = async { repository.fetchRepos(username) }
                    state.update { it.success(user.await(), repos.await()) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: NetworkException) {
                state.update {
                    it.failure(ErrorMessage.CanRetry(RepoError.NetworkError) { fetchRepos(username) })
                }
            } catch (e: Exception) {
                state.update {
                    it.failure(ErrorMessage.CanRetry(RepoError.UnknownError) { fetchRepos(username) })
                }
            }
        }
    }

    fun onErrorDismissed() {
        state.update { it.idle() }
    }
}