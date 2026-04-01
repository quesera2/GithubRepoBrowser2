package que.sera.sera.githubbrowser2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

class GitHubRepositoryImplTest : DescribeSpec({
    val testJson = Json { ignoreUnknownKeys = true }

    fun buildRepository(engine: MockEngine): GitHubRepositoryImpl {
        val client = HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(testJson) }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.github.com"
                }
            }
        }
        return GitHubRepositoryImpl(GitHubApi(client))
    }

    describe("fetchUser") {
        context("IOExceptionが発生したとき") {
            it("RepositoryExceptionにラップされる") {
                val repository = buildRepository(MockEngine { throw IOException("network error") })
                shouldThrow<RepositoryException> {
                    repository.fetchUser("testuser")
                }
            }
        }

        context("200 OKが返ったがJSONが不正だった場合") {
            it("RepositoryExceptionにラップされる") {
                val repository = buildRepository(MockEngine {
                    respond(
                        content = """{"message":"ok"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                })
                shouldThrow<RepositoryException> {
                    repository.fetchUser("testuser")
                }
            }
        }

        context("404 Not Foundが返ったとき") {
            it("RepositoryExceptionにラップされる") {
                val repository = buildRepository(MockEngine {
                    respond(
                        content = """{"message":"Not Found"}""",
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                })
                shouldThrow<RepositoryException> {
                    repository.fetchUser("testuser")
                }
            }
        }

        context("500 Internal Server Errorが返ったとき") {
            it("RepositoryExceptionにラップされる") {
                val repository = buildRepository(MockEngine {
                    respond(
                        content = """{"message":"Internal Server Error"}""",
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                })
                shouldThrow<RepositoryException> {
                    repository.fetchUser("testuser")
                }
            }
        }
    }
})
