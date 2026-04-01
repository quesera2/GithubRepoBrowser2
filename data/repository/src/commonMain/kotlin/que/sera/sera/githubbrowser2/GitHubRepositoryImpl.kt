package que.sera.sera.githubbrowser2

import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.io.IOException

class GitHubRepositoryImpl(
    private val api: GitHubApi
) : GitHubRepository {

    override suspend fun fetchUser(username: String): GitHubUser =
        runCatchingRepository { api.fetchUser(username) }

    override suspend fun fetchRepos(username: String): List<GitHubRepo> =
        runCatchingRepository { api.fetchRepos(username) }

    override suspend fun fetchTrendingRepos(): List<GitHubRepo> =
        runCatchingRepository { api.fetchTrendingRepos().items }

    private suspend fun <T> runCatchingRepository(block: suspend () -> T): T = try {
        block()
    } catch (e: ResponseException) {
        throw RepositoryException(e)
    } catch (e: IOException) {
        throw RepositoryException(e)
    } catch (e: JsonConvertException) {
        throw RepositoryException(e)
    }
}
