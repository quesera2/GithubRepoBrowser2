package que.sera.sera.githubbrowser2.repository

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.zacsweers.metrox.viewmodel.metroViewModel
import que.sera.sera.githubbrowser2.GitHubRepo
import que.sera.sera.githubbrowser2.RepoViewModel
import que.sera.sera.githubbrowser2.RepoViewState

@Composable
fun RepositoryViewScreen(
    viewModel: RepoViewModel = metroViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }

    RepositoryViewContent(
        uiState = uiState,
        query = query,
        onQueryChange = { query = it },
        onSearch = { viewModel.fetchRepos(query) },
        onRetry = { viewModel.fetchRepos(query) },
        onDismissErrorDialog = { viewModel.onErrorDismissed() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepositoryViewContent(
    uiState: RepoViewState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onDismissErrorDialog: () -> Unit,
) {
    val repos = uiState.repos
    val title = if (repos.isNullOrEmpty()) "リポジトリ一覧" else "リポジトリ一覧（${repos.size}件）"

    MaterialTheme {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(title = { Text(title) })
                    SearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = query,
                                onQueryChange = onQueryChange,
                                onSearch = { onSearch() },
                                expanded = false,
                                onExpandedChange = {},
                                placeholder = { Text("ユーザー名を入力してください") },
                            )
                        },
                        expanded = false,
                        onExpandedChange = {},
                        windowInsets = WindowInsets(0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {}
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                RepoListContent(repos = repos)

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (uiState.isError) {
                    AlertDialog(
                        onDismissRequest = onDismissErrorDialog,
                        title = { Text("エラー") },
                        text = { Text(uiState.errorMessage) },
                        confirmButton = {
                            TextButton(onClick = onRetry) { Text("再試行") }
                        },
                        dismissButton = {
                            TextButton(onClick = onDismissErrorDialog) { Text("閉じる") }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RepoListContent(
    repos: List<GitHubRepo>?
) {
    when {
        repos == null -> Unit
        repos.isEmpty() -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "リポジトリが見つかりません",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(repos) { repo ->
                RepoListViewItem(repo)
                HorizontalDivider()
            }
        }
    }
}

// ---- Previews ----

private class RepoViewStateProvider : PreviewParameterProvider<RepoViewState> {
    private val named = listOf(
        "Idle" to RepoViewState(),
        "Loading" to RepoViewState().loading(),
        "Success" to RepoViewState().success(sampleRepos),
        "Empty" to RepoViewState().success(emptyList()),
        "Error" to RepoViewState().failure("Not Found"),
    )
    override val values = named.map { it.second }.asSequence()
    override fun getDisplayName(index: Int) = named[index].first
}

@Preview(showBackground = true)
@Composable
private fun PreviewRepositoryView(@PreviewParameter(RepoViewStateProvider::class) uiState: RepoViewState) {
    RepositoryViewContent(
        uiState = uiState,
        query = "JetBrains",
        onQueryChange = {},
        onSearch = {},
        onRetry = {},
        onDismissErrorDialog = {}
    )
}

private val sampleRepos = listOf(
    GitHubRepo(
        id = 1,
        name = "kotlin",
        fullName = "JetBrains/kotlin",
        description = "The Kotlin Programming Language",
        stars = 50000,
        forks = 6000,
        language = "Kotlin",
        htmlUrl = ""
    ),
    GitHubRepo(
        id = 2,
        name = "compose",
        fullName = "JetBrains/compose",
        description = "Compose Multiplatform",
        stars = 12000,
        forks = 800,
        language = "Kotlin",
        htmlUrl = ""
    ),
    GitHubRepo(
        id = 3,
        name = "ktor",
        fullName = "ktorio/ktor",
        description = null,
        stars = 13000,
        forks = 1100,
        language = null,
        htmlUrl = ""
    ),
)