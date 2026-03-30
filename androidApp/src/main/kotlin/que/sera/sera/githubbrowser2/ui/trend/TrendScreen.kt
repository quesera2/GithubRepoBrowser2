package que.sera.sera.githubbrowser2.ui.trend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.desc
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable
import que.sera.sera.githubbrowser2.ErrorMessage
import que.sera.sera.githubbrowser2.GitHubRepo
import que.sera.sera.githubbrowser2.MR
import que.sera.sera.githubbrowser2.TrendViewModel
import que.sera.sera.githubbrowser2.TrendViewState
import que.sera.sera.githubbrowser2.ui.component.ErrorDialog
import que.sera.sera.githubbrowser2.ui.search.RepoListViewItem

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
                title = { Text(stringResource(MR.strings.trending_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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
                            TrendListViewItem(rank = index + 1, repo = repo)
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
                ErrorDialog(
                    errorMessage = errorMessage,
                    onDismiss = onDismissErrorDialog,
                )
            }
        }
    }
}

@Composable
private fun TrendListViewItem(rank: Int, repo: GitHubRepo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 28.dp, minHeight = 22.dp)
                .background(
                    color = if (rank <= 3) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp),
                )
                .padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (rank <= 3) MaterialTheme.colorScheme.onTertiaryContainer
                else MaterialTheme.colorScheme.onPrimary,
            )
        }
        RepoListViewItem(repo = repo, modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTrendScreen(
    @PreviewParameter(TrendViewStateProvider::class) uiState: TrendViewState
) {
    MaterialTheme {
        TrendContent(uiState = uiState, onDismissErrorDialog = {})
    }
}

private class TrendViewStateProvider : PreviewParameterProvider<TrendViewState> {
    private val named = listOf(
        "Loading" to TrendViewState().loading(),
        "Success" to TrendViewState().success(sampleRepos),
        "Empty" to TrendViewState().success(emptyList()),
        "Error" to TrendViewState().failure(ErrorMessage.CanRetry("Network error".desc()) {}),
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
