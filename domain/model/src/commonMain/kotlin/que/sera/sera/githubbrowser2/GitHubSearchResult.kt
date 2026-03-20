package que.sera.sera.githubbrowser2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubSearchResult(
    @SerialName("total_count") val totalCount: Int,
    val items: List<GitHubRepo>,
)