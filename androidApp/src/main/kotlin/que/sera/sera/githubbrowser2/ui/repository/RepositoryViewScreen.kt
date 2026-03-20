package que.sera.sera.githubbrowser2.ui.repository

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.zacsweers.metrox.viewmodel.metroViewModel
import que.sera.sera.githubbrowser2.GitHubRepo
import que.sera.sera.githubbrowser2.R
import que.sera.sera.githubbrowser2.RepoViewModel
import que.sera.sera.githubbrowser2.RepoViewState

@Composable
fun RepositoryViewScreen(
    viewModel: RepoViewModel = metroViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    RepositoryViewContent(
        uiState = uiState,
        onSearch = { viewModel.fetchRepos(it) },
        onDismissErrorDialog = { viewModel.onErrorDismissed() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepositoryViewContent(
    uiState: RepoViewState,
    onSearch: (String) -> Unit,
    onDismissErrorDialog: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val repos = uiState.repos
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Search")
                            Text(
                                text = "Find repositories by username",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = query,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.icon_search),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                            onQueryChange = { query = it },
                            onSearch = {
                                keyboardController?.hide()
                                onSearch(it)
                            },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text("Search username") },
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    windowInsets = WindowInsets(0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {}
            }
        }
    ) { innerPadding ->
        val topPaddingPx = with(LocalDensity.current) {
            innerPadding.calculateTopPadding().toPx()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            RepoListContent(
                repos = repos,
                isLoading = uiState.isLoading,
                innerPadding = innerPadding
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(innerPadding.calculateTopPadding())
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                Color.Transparent,
                            ),
                            startY = topPaddingPx * 0.3f,
                        )
                    )
            )

            if (uiState.isError) {
                AlertDialog(
                    onDismissRequest = onDismissErrorDialog,
                    title = { Text("Error") },
                    text = { Text(uiState.errorMessage) },
                    confirmButton = {
                        TextButton(onClick = { onSearch(query) }) { Text("Retry") }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismissErrorDialog) { Text("Close") }
                    },
                )
            }
        }
    }
}

@Composable
private fun RepoListContent(
    repos: List<GitHubRepo>?,
    isLoading: Boolean,
    innerPadding: PaddingValues,
) {
    when {
        repos == null -> if (!isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(vertical = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyView()
            }
        }

        repos.isEmpty() -> Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No repositories found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentPadding = innerPadding,
        ) {
            items(repos) { repo ->
                RepoListViewItem(repo)
                HorizontalDivider()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_search),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(44.dp),
        )
        Text(
            text = "Search for a GitHub user",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRepositoryView(
    @PreviewParameter(RepoViewStateProvider::class) uiState: RepoViewState
) {
    MaterialTheme {
        RepositoryViewContent(
            uiState = uiState,
            onSearch = {},
            onDismissErrorDialog = {}
        )
    }
}

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