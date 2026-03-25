package que.sera.sera.githubbrowser2

interface GitHubRepository {
    suspend fun fetchRepos(username: String): List<GitHubRepo>
    suspend fun fetchTrendingRepos(): List<GitHubRepo>
}
