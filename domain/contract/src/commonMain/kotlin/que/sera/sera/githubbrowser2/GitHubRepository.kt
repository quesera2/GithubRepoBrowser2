package que.sera.sera.githubbrowser2

interface GitHubRepository {
    suspend fun fetchUserAndRepos(username: String): Pair<GitHubUser, List<GitHubRepo>>
    suspend fun fetchTrendingRepos(): List<GitHubRepo>
}
