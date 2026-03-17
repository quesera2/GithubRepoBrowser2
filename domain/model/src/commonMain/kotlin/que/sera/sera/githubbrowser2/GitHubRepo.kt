package que.sera.sera.githubbrowser2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepo(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    @SerialName("stargazers_count") val stars: Int,
    @SerialName("forks_count") val forks: Int,
    val language: String? = null,
    @SerialName("html_url") val htmlUrl: String,
)
