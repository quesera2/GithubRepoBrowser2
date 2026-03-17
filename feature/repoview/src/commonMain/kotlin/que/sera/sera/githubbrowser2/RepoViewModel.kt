package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepoViewModel(
    private val repository: GitHubRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<RepoUiState>(RepoUiState.Idle)

    @NativeCoroutinesState
    val uiState: StateFlow<RepoUiState> = _uiState

    fun fetchRepos(username: String) {
        viewModelScope.launch {
            _uiState.value = RepoUiState.Loading
            _uiState.value = try {
                RepoUiState.Success(repository.fetchRepos(username))
            } catch (e: Exception) {
                RepoUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}