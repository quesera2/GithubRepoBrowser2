package que.sera.sera.githubbrowser2.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import que.sera.sera.githubbrowser2.GitHubRepo
import que.sera.sera.githubbrowser2.R
import que.sera.sera.githubbrowser2.formatCount
import que.sera.sera.githubbrowser2.languageColor
import androidx.compose.material3.Icon
import que.sera.sera.githubbrowser2.ui.theme.GitHubBrowserTheme

@Composable
internal fun RepoListViewItem(
    repo: GitHubRepo,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.minimumInteractiveComponentSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = repo.fullName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            repo.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repo.language?.let { lang ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(languageColor(repo.language)), CircleShape)
                        )
                        Text(
                            text = lang,
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
                        painter = painterResource(R.drawable.icon_star),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = repo.stars.formatCount().localized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_fork),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = repo.forks.formatCount().localized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRepoListViewItem() {
    GitHubBrowserTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RepoListViewItem(
                repo = GitHubRepo(
                    id = 1,
                    name = "kotlin",
                    fullName = "JetBrains/kotlin",
                    description = "The Kotlin Programming Language",
                    stars = 50000,
                    forks = 6000,
                    language = "Kotlin",
                    htmlUrl = "",
                )
            )

            RepoListViewItem(
                repo = GitHubRepo(
                    id = 2,
                    name = "dotfiles",
                    fullName = "user/dotfiles",
                    description = null,
                    stars = 0,
                    forks = 0,
                    language = null,
                    htmlUrl = "",
                )
            )
        }
    }
}
