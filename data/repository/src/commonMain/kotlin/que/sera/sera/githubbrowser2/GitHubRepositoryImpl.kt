package que.sera.sera.githubbrowser2

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class GitHubRepositoryImpl(
    private val api: GitHubApi
) : GitHubRepository {
    override suspend fun fetchRepos(username: String): List<GitHubRepo> =
        api.fetchRepos(username)
}
