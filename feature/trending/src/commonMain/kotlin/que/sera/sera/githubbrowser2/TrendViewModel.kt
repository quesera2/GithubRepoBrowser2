package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.desc
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import que.sera.sera.githubbrowser2.MR

@Inject
class TrendViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    val state: StateFlow<TrendViewState>
        field = MutableStateFlow(TrendViewState())

    fun fetchTrending() {
        viewModelScope.launch {
            state.update { it.loading() }
            try {
                val repos = repository.fetchTrendingRepos()
                state.update { it.success(repos) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = e.message?.desc() ?: MR.strings.unknown_error.desc()
                state.update {
                    it.failure(
                        ErrorMessage.CanRetry(
                            message = message,
                            retryAction = { fetchTrending() })
                    )
                }
            }
        }
    }

    fun onErrorDismissed() {
        state.update { it.idle() }
    }
}
