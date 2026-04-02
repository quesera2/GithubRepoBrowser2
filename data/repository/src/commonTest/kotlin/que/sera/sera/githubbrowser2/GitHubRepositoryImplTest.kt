package que.sera.sera.githubbrowser2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import kotlinx.io.IOException

class GitHubRepositoryImplTest : DescribeSpec({
    describe("fetchUser") {
        context("IOExceptionが発生したとき") {
            it("RepositoryExceptionにラップされる") {
                val repository = GitHubRepositoryImpl(
                    FakeGitHubApi { throw IOException("network error") }
                )
                shouldThrow<RepositoryException> { repository.fetchUser("testuser") }
            }
        }

        context("JsonConvertExceptionが発生したとき") {
            it("RepositoryExceptionにラップされる") {
                val repository = GitHubRepositoryImpl(
                    FakeGitHubApi { throw JsonConvertException("bad json") }
                )
                shouldThrow<RepositoryException> { repository.fetchUser("testuser") }
            }
        }

        context("404 Not Foundが返ったとき") {
            it("RepositoryExceptionにラップされる") {
                val exception = createResponseException(HttpStatusCode.NotFound)
                val repository = GitHubRepositoryImpl(FakeGitHubApi { throw exception })
                shouldThrow<RepositoryException> { repository.fetchUser("testuser") }
            }
        }

        context("500 Internal Server Errorが返ったとき") {
            it("RepositoryExceptionにラップされる") {
                val exception = createResponseException(HttpStatusCode.InternalServerError)
                val repository = GitHubRepositoryImpl(FakeGitHubApi { throw exception })
                shouldThrow<RepositoryException> { repository.fetchUser("testuser") }
            }
        }
    }
}) {
    companion object {
        private class FakeGitHubApi(
            private val onFetchUser: suspend (String) -> GitHubUser,
        ) : GitHubApi {
            override suspend fun fetchUser(username: String): GitHubUser = onFetchUser(username)
            override suspend fun fetchRepos(username: String, perPage: Int, sort: String) = emptyList<GitHubRepo>()
            override suspend fun fetchTrendingRepos(language: String?, perPage: Int, page: Int) = GitHubSearchResult(0, emptyList())
        }

        private suspend fun createResponseException(statusCode: HttpStatusCode): ResponseException {
            val engine = MockEngine { respond("error", status = statusCode) }
            val client = HttpClient(engine) { expectSuccess = true }
            return try {
                client.get("https://api.github.com/test")
                error("unreachable")
            } catch (e: ResponseException) {
                e
            } finally {
                client.close()
            }
        }
    }
}