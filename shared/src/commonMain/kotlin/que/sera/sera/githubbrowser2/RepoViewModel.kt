package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RepoUiState {
    data object Idle : RepoUiState()
    data object Loading : RepoUiState()
    data class Success(val repos: List<GitHubRepo>) : RepoUiState()
    data class Error(val message: String) : RepoUiState()
}

class RepoViewModel : ViewModel() {
    private val api = GitHubApi()

    private val _uiState = MutableStateFlow<RepoUiState>(RepoUiState.Idle)
    @NativeCoroutinesState
    val uiState: StateFlow<RepoUiState> = _uiState

    fun fetchRepos(username: String) {
        viewModelScope.launch {
            _uiState.value = RepoUiState.Loading
            _uiState.value = try {
                RepoUiState.Success(api.fetchRepos(username))
            } catch (e: Exception) {
                RepoUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
