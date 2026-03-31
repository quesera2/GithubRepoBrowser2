package que.sera.sera.githubbrowser2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUser(
    val name: String,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
)