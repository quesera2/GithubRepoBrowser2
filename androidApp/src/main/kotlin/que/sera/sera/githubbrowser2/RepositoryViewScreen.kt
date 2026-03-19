package que.sera.sera.githubbrowser2

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

@Composable
fun RepositoryViewScreen(
    vm: RepoViewModel = metroViewModel()
) {
    val uiState by vm.state.collectAsState()
    var query by remember { mutableStateOf("") }

    RepositoryViewContent(
        uiState = uiState,
        query = query,
        onQueryChange = { query = it },
        onSearch = { vm.fetchRepos(query) },
        onRetry = { vm.fetchRepos(query) },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepositoryViewContent(
    uiState: RepoUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
) {
    val title = if (uiState is RepoUiState.Success) {
        "リポジトリ一覧（${uiState.repos.size}件）"
    } else {
        "リポジトリ一覧"
    }

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
                when (val state = uiState) {
                    is RepoUiState.Idle -> Unit
                    is RepoUiState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }

                    is RepoUiState.Error -> AlertDialog(
                        onDismissRequest = {},
                        title = { Text("エラー") },
                        text = { Text(state.message) },
                        confirmButton = {
                            TextButton(onClick = onRetry) { Text("再試行") }
                        },
                        dismissButton = {
                            TextButton(onClick = {}) { Text("閉じる") }
                        },
                    )

                    is RepoUiState.Success -> if (state.repos.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "リポジトリが見つかりません",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        LazyColumn {
                            items(state.repos) { repo ->
                                RepoRow(repo)
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---- Previews ----

private val sampleRepos = listOf(
    GitHubRepo(
        1,
        "kotlin",
        "JetBrains/kotlin",
        "The Kotlin Programming Language",
        50000,
        6000,
        "Kotlin",
        ""
    ),
    GitHubRepo(
        2,
        "compose",
        "JetBrains/compose",
        "Compose Multiplatform",
        12000,
        800,
        "Kotlin",
        ""
    ),
    GitHubRepo(3, "ktor", "ktorio/ktor", null, 13000, 1100, null, ""),
)

private class RepoUiStateProvider : PreviewParameterProvider<RepoUiState> {
    private val named = listOf(
        "Loading" to RepoUiState.Loading,
        "Success" to RepoUiState.Success(sampleRepos),
        "Empty" to RepoUiState.Success(emptyList()),
        "Error" to RepoUiState.Error("Not Found"),
    )
    override val values = named.map { it.second }.asSequence()
    override fun getDisplayName(index: Int) = named[index].first
}

@Preview(showBackground = true)
@Composable
private fun PreviewRepositoryView(@PreviewParameter(RepoUiStateProvider::class) uiState: RepoUiState) {
    RepositoryViewContent(
        uiState = uiState,
        query = "JetBrains",
        onQueryChange = {}, onSearch = {}, onRetry = {},
    )
}

@Composable
private fun RepoRow(repo: GitHubRepo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(repo.name, style = MaterialTheme.typography.titleMedium)

        repo.description?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repo.language?.let { lang ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.padding(0.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        lang,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${repo.stars}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.weight(1f))
        }
    }
}