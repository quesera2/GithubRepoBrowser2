package que.sera.sera.githubbrowser2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
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

class GitHubApiKtorImplTest : DescribeSpec({
    lateinit var testJson: Json
    lateinit var api: GitHubApi

    beforeSpec {
        testJson = Json { ignoreUnknownKeys = true }
    }

    describe("searchReposFromUser") {
        context("IOExceptionが発生したとき") {
            beforeEach {
                api = setupApi(
                    json = testJson,
                    engine = MockEngine { throw IOException("network error") }
                )
            }

            it("GitHubApiExceptionにラップされる") {
                shouldThrow<GitHubApiException> {
                    api.searchReposFromUser("testuser")
                }
            }
        }

        context("200 OKが返ったとき") {
            beforeEach {
                api = setupApi(
                    json = testJson,
                    engine = MockEngine { request ->
                        when {
                            request.url.encodedPath.endsWith("/repos") -> respond(
                                content = """[{"id":1,"name":"repo1","full_name":"testuser/repo1","description":"desc","stargazers_count":10,"forks_count":2,"language":"Kotlin","html_url":"https://github.com/testuser/repo1","pushed_at":"2024-01-01T00:00:00Z"}]""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                            else -> respond(
                                content = """{"name":"Test User","login":"testuser","avatar_url":"https://example.com/avatar.png"}""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }
                    }
                )
            }

            it("Pair<GitHubUser, List<GitHubRepo>>を返す") {
                val (user, repos) = api.searchReposFromUser("testuser")
                user.login shouldBe "testuser"
                repos.first().name shouldBe "repo1"
            }
        }

        context("200 OKが返ったがJSONが不正だった場合") {
            beforeEach {
                api = setupApi(
                    json = testJson,
                    engine = MockEngine { request ->
                        when {
                            request.url.encodedPath.endsWith("/repos") -> respond(
                                content = """[{"message":"ok"}]""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                            else -> respond(
                                content = """{"message":"ok"}""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                            )
                        }
                    }
                )
            }

            it("GitHubApiExceptionにラップされる") {
                shouldThrow<GitHubApiException> {
                    api.searchReposFromUser("testuser")
                }
            }
        }

        context("404 Not Foundが返ったとき") {
            beforeEach {
                api = setupApi(
                    json = testJson,
                    engine = MockEngine {
                        respond(
                            content = """{"message":"Not Found","documentation_url":"https://docs.github.com/rest"}""",
                            status = HttpStatusCode.NotFound,
                            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                        )
                    }
                )
            }

            it("GitHubApiExceptionにラップされる") {
                shouldThrow<GitHubApiException> {
                    api.searchReposFromUser("testuser")
                }
            }
        }

        context("500 Internal Server Errorが返ったとき") {
            beforeEach {
                api = setupApi(
                    json = testJson,
                    engine = MockEngine {
                        respond(
                            content = """{"message":"Internal Server Error"}""",
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                        )
                    }
                )
            }

            it("GitHubApiExceptionにラップされる") {
                shouldThrow<GitHubApiException> {
                    api.searchReposFromUser("testuser")
                }
            }
        }
    }
}) {

    companion object {
        private fun setupApi(
            json: Json,
            engine: HttpClientEngine,
        ) = GitHubApiKtorImpl(
            httpClient = HttpClient(engine) {
                expectSuccess = true
                install(ContentNegotiation) { json(json) }
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = "api.github.com"
                    }
                }
            }
        )
    }
}