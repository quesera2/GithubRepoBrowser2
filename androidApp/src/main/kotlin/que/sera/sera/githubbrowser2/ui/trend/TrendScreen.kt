package que.sera.sera.githubbrowser2.ui.trend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import dev.icerock.moko.resources.compose.stringResource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable
import que.sera.sera.githubbrowser2.ErrorMessage
import que.sera.sera.githubbrowser2.GitHubRepo
import que.sera.sera.githubbrowser2.TrendError
import que.sera.sera.githubbrowser2.MR
import que.sera.sera.githubbrowser2.TrendViewModel
import que.sera.sera.githubbrowser2.TrendViewState
import que.sera.sera.githubbrowser2.ui.component.ErrorDialog
import que.sera.sera.githubbrowser2.ui.component.RepoListViewItem
import que.sera.sera.githubbrowser2.ui.theme.GitHubBrowserTheme

@Serializable
data object RouteTrend : NavKey

@Composable
fun TrendScreen(
    viewModel: TrendViewModel = metroViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchTrending() }
    TrendContent(
        uiState = uiState,
        onDismissErrorDialog = { viewModel.onErrorDismissed() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendContent(
    uiState: TrendViewState,
    onDismissErrorDialog: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(MR.strings.trending_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            val repos = uiState.repos
            when {
                repos == null -> {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                repos.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(MR.strings.no_trending_repositories_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding() + 8.dp,
                            bottom = innerPadding.calculateBottomPadding() + 8.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsIndexed(repos) { index, repo ->
                            RepoListViewItem(repo = repo, rank = index + 1)
                        }
                    }
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

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

            uiState.errorMessage?.let { errorMessage ->
                val message = when (val error = errorMessage.error) {
                    is TrendError.NetworkError -> stringResource(MR.strings.network_error)
                    is TrendError.UnknownError -> stringResource(MR.strings.unknown_error)
                }
                ErrorDialog(errorMessage = errorMessage, message = message, onDismiss = onDismissErrorDialog)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewTrendScreen(
    @PreviewParameter(TrendViewStateProvider::class) uiState: TrendViewState
) {
    GitHubBrowserTheme {
        TrendContent(uiState = uiState, onDismissErrorDialog = {})
    }
}

private class TrendViewStateProvider : PreviewParameterProvider<TrendViewState> {
    private val named = listOf(
        "Loading" to TrendViewState().loading(),
        "Success" to TrendViewState().success(sampleRepos),
        "Empty" to TrendViewState().success(emptyList()),
        "Error" to TrendViewState().failure(ErrorMessage.CanRetry(TrendError.NetworkError) {}),
    )
    override val values = named.map { it.second }.asSequence()
    override fun getDisplayName(index: Int) = named[index].first
}

private val sampleRepos = listOf(
    GitHubRepo(
        id = 1, name = "kotlin", fullName = "JetBrains/kotlin",
        description = "The Kotlin Programming Language", stars = 50000, forks = 6000,
        language = "Kotlin", htmlUrl = ""
    ),
    GitHubRepo(
        id = 2, name = "swift", fullName = "apple/swift",
        description = "The Swift Programming Language", stars = 67000, forks = 10000,
        language = "C++", htmlUrl = ""
    ),
    GitHubRepo(
        id = 3, name = "linux", fullName = "torvalds/linux",
        description = null, stars = 180000, forks = 55000,
        language = "C", htmlUrl = ""
    ),
)
