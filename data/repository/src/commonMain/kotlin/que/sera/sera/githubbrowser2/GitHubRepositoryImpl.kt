package que.sera.sera.githubbrowser2

class GitHubRepositoryImpl(
    private val api: GitHubApi
) : GitHubRepository {
    override suspend fun fetchRepos(username: String): List<GitHubRepo> =
        api.fetchRepos(username)
}