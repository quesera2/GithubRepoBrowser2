package que.sera.sera.githubbrowser2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import que.sera.sera.githubbrowser2.GitHubRepo
import que.sera.sera.githubbrowser2.R
import que.sera.sera.githubbrowser2.formatCount
import que.sera.sera.githubbrowser2.languageColor
import que.sera.sera.githubbrowser2.ui.theme.GitHubBrowserTheme

@Composable
internal fun RepoListViewItem(
    repo: GitHubRepo,
    modifier: Modifier = Modifier,
    rank: Int? = null,
) {
    OutlinedCard(
        modifier = modifier.minimumInteractiveComponentSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (rank != null) {
                    RankBox(rank)

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                            append(repo.fullName.substringBefore('/'))
                        }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                            append("/")
                        }
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(repo.name)
                        }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

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
                    MetaChip(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(languageColor(lang)), CircleShape)
                            )
                        },
                        text = { Text(text = lang) }
                    )
                }

                MetaChip(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_star),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(10.dp)
                        )
                    },
                    text = { Text(text = repo.stars.formatCount().localized()) }
                )

                MetaChip(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_fork),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp)
                        )
                    },
                    text = { Text(text = repo.forks.formatCount().localized()) }
                )
            }
        }
    }
}

@Composable
private fun RankBox(rank: Int) {
    val (boxColor, textColor) = with(MaterialTheme.colorScheme) {
        if (rank <= 3) {
            tertiaryContainer to onTertiaryContainer
        } else {
            primary to onPrimary
        }
    }

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 28.dp, minHeight = 22.dp)
            .background(
                color = boxColor,
                shape = RoundedCornerShape(6.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun MetaChip(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
) = Row(
    modifier = Modifier
        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        ProvideTextStyle(MaterialTheme.typography.labelSmall) {
            icon()
            text()
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
                    description = "The Kotlin Programming Language The Kotlin Programming Language The Kotlin Programming Language",
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

            RepoListViewItem(
                rank = 1,
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
                rank = 4,
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
