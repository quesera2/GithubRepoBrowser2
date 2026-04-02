package que.sera.sera.githubbrowser2


class GitHubRepositoryImpl(
    private val api: GitHubApi
) : GitHubRepository {

    override suspend fun fetchUserAndRepos(
        username: String
    ): Pair<GitHubUser, List<GitHubRepo>> = runCatchingRepository {
        api.searchReposFromUser(username)
    }

    override suspend fun fetchTrendingRepos(): List<GitHubRepo> =
        runCatchingRepository { api.fetchTrendingRepos().items }

    private suspend fun <T> runCatchingRepository(block: suspend () -> T): T = try {
        block()
    } catch (e: GitHubApiException) {
        throw RepositoryException(e)
    }
}
