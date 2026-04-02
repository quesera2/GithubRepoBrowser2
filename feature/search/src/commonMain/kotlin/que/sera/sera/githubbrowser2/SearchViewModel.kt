package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
class SearchViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    val state: StateFlow<SearchViewState>
        field = MutableStateFlow(SearchViewState())

    fun fetchRepos(username: String) {
        if (username.isEmpty()) {
            state.update {
                it.failure(ErrorMessage.CancelOnly(SearchViewError.EmptyUsername))
            }
            return
        }

        viewModelScope.launch {
            state.update { it.loading() }
            try {
                val (user, repos) = repository.fetchUserAndRepos(username)
                state.update { it.success(user, repos) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: RepositoryException) {
                state.update {
                    it.failure(ErrorMessage.CanRetry(SearchViewError.NetworkError) {
                        fetchRepos(username)
                    })
                }
            } catch (e: Exception) {
                state.update {
                    it.failure(ErrorMessage.CanRetry(SearchViewError.UnknownError) {
                        fetchRepos(username)
                    })
                }
            }
        }
    }

    fun onErrorDismissed() {
        state.update { it.idle() }
    }
}