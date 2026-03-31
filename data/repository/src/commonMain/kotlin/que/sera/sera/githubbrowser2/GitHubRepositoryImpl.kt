package que.sera.sera.githubbrowser2

class GitHubRepositoryImpl(
    private val api: GitHubApi
) : GitHubRepository {
    override suspend fun fetchUser(username: String): GitHubUser =
        api.fetchUser(username)

    override suspend fun fetchRepos(username: String): List<GitHubRepo> =
        api.fetchRepos(username)

    override suspend fun fetchTrendingRepos(): List<GitHubRepo> =
        api.fetchTrendingRepos().items
}