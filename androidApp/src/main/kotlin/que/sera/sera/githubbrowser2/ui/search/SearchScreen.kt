package que.sera.sera.githubbrowser2.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable
import que.sera.sera.githubbrowser2.ErrorMessage
import que.sera.sera.githubbrowser2.GitHubRepo
import que.sera.sera.githubbrowser2.RepoError
import que.sera.sera.githubbrowser2.GitHubUser
import que.sera.sera.githubbrowser2.MR
import que.sera.sera.githubbrowser2.R
import que.sera.sera.githubbrowser2.RepoViewModel
import que.sera.sera.githubbrowser2.RepoViewState
import que.sera.sera.githubbrowser2.ui.component.ErrorDialog
import que.sera.sera.githubbrowser2.ui.component.RepoListViewItem
import que.sera.sera.githubbrowser2.ui.theme.GitHubBrowserTheme

@Serializable
data object RouteSearch : NavKey

@Composable
fun SearchScreen(
    viewModel: RepoViewModel = metroViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    SearchContent(
        uiState = uiState,
        onSearch = { viewModel.fetchRepos(it) },
        onDismissErrorDialog = { viewModel.onErrorDismissed() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
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
                        Text(
                            text = stringResource(MR.strings.search_title),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                )
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = query,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.icon_search),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                            trailingIcon = {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { query = "" }) {
                                        Icon(
                                            painter = painterResource(R.drawable.icon_close),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                            onQueryChange = { query = it },
                            onSearch = {
                                keyboardController?.hide()
                                onSearch(it)
                            },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text(stringResource(MR.strings.search_placeholder)) },
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    windowInsets = WindowInsets(0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                ) {}
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            RepoListContent(
                user = uiState.user,
                repos = repos,
                isLoading = uiState.isLoading,
                innerPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(innerPadding.calculateTopPadding())
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                            )
                        )
                    )
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.errorMessage?.let { errorMessage ->
                val message = when (val error = errorMessage.error) {
                    is RepoError.EmptyUsername -> stringResource(MR.strings.please_enter_username)
                    is RepoError.NetworkError -> stringResource(MR.strings.network_error)
                    is RepoError.UnknownError -> stringResource(MR.strings.unknown_error)
                }
                ErrorDialog(errorMessage = errorMessage, message = message, onDismiss = onDismissErrorDialog)
            }
        }
    }
}

@Composable
private fun RepoListContent(
    user: GitHubUser?,
    repos: List<GitHubRepo>?,
    isLoading: Boolean,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) = when {
    repos == null -> {
        Box(
            modifier = modifier
                .padding(innerPadding)
                .padding(vertical = 60.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!isLoading) {
                EmptyView()
            }
        }
    }

    repos.isEmpty() -> Box(
        modifier = modifier
            .padding(innerPadding)
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(MR.strings.no_repositories_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    else -> LazyColumn(
        modifier = modifier.imePadding(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = innerPadding.calculateTopPadding() + 8.dp,
            bottom = innerPadding.calculateBottomPadding() + 8.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (user != null) {
            item { UserHeader(user) }
        }
        items(repos) { repo ->
            RepoListViewItem(repo)
        }
    }
}

@Composable
private fun UserHeader(user: GitHubUser) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun EmptyView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_search),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(44.dp),
        )
        Text(
            text = stringResource(MR.strings.search_for_user),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearch(
    @PreviewParameter(RepoViewStateProvider::class) uiState: RepoViewState
) {
    GitHubBrowserTheme {
        SearchContent(
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
        "Success" to RepoViewState().success(sampleUser, sampleRepos),
        "Empty" to RepoViewState().success(sampleUser, emptyList()),
        "Retry Error" to RepoViewState().failure(ErrorMessage.CanRetry(RepoError.NetworkError) {}),
        "Cancel Error" to RepoViewState().failure(ErrorMessage.CancelOnly(RepoError.EmptyUsername)),
    )
    override val values = named.map { it.second }.asSequence()
    override fun getDisplayName(index: Int) = named[index].first
}

private val sampleUser = GitHubUser(
    login = "jetbrains",
    name = "JetBrains",
    avatarUrl = "https://avatars.githubusercontent.com/u/4314696",
)

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