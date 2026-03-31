package que.sera.sera.githubbrowser2

interface GitHubRepository {
    suspend fun fetchUser(username: String): GitHubUser
    suspend fun fetchRepos(username: String): List<GitHubRepo>
    suspend fun fetchTrendingRepos(): List<GitHubRepo>
}
