package que.sera.sera.githubbrowser2

class FakeGitHubRepository(
    private val result: Result<List<GitHubRepo>> = Result.success(emptyList()),
    private val userResult: Result<GitHubUser> = Result.success(FAKE_USER),
) : GitHubRepository {
    override suspend fun fetchUserAndRepos(username: String): Pair<GitHubUser, List<GitHubRepo>> =
        userResult.getOrThrow() to result.getOrThrow()

    override suspend fun fetchTrendingRepos(): List<GitHubRepo> = result.getOrThrow()
}

val FAKE_USER = GitHubUser(
    name = "Test User",
    login = "testuser",
    avatarUrl = "",
)
