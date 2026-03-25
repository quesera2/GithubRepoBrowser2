package que.sera.sera.githubbrowser2

class FakeGitHubRepository(
    private val result: Result<List<GitHubRepo>> = Result.success(emptyList())
) : GitHubRepository {
    override suspend fun fetchRepos(username: String): List<GitHubRepo> = result.getOrThrow()
    override suspend fun fetchTrendingRepos(): List<GitHubRepo> = result.getOrThrow()
}
