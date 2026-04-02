package que.sera.sera.githubbrowser2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.io.IOException
import que.sera.sera.githubbrowser2.SortOrder.CREATED_AT
import que.sera.sera.githubbrowser2.SortOrder.NAME
import que.sera.sera.githubbrowser2.SortOrder.PUSHED_AT
import que.sera.sera.githubbrowser2.SortOrder.STARGAZERS
import que.sera.sera.githubbrowser2.SortOrder.UPDATED_AT
import kotlin.time.Clock

class GitHubApiKtorImpl(
    private val httpClient: HttpClient,
) : GitHubApi {

    override suspend fun searchReposFromUser(
        username: String,
        perPage: Int,
        sort: SortOrder,
    ): Pair<GitHubUser, List<GitHubRepo>> = runCatchingApi {
        coroutineScope {
            val userName = async {
                httpClient.get("users/$username").body<GitHubUser>()
            }
            val repos = async {
                httpClient.get("users/$username/repos") {
                    url {
                        parameters.append("per_page", perPage.toString())
                        parameters.append("sort", sort.toParam())
                    }
                }.body<List<GitHubRepo>>()
            }
            userName.await() to repos.await()
        }
    }

    private suspend fun <T> runCatchingApi(block: suspend () -> T): T = try {
        block()
    } catch (e: ResponseException) {
        throw GitHubApiException(e)
    } catch (e: IOException) {
        throw GitHubApiException(e)
    } catch (e: JsonConvertException) {
        throw GitHubApiException(e)
    }

    override suspend fun fetchTrendingRepos(
        language: String?,
        perPage: Int,
        page: Int,
    ): GitHubSearchResult = runCatchingApi {
        val since = Clock.System.todayIn(TimeZone.UTC).minus(1, DateTimeUnit.MONTH)
        val query = buildString {
            append("pushed:>$since")
            if (language != null) append("+language:$language")
        }
        httpClient.get("search/repositories") {
            url {
                parameters.append("q", query)
                parameters.append("sort", "stars")
                parameters.append("order", "desc")
                parameters.append("per_page", perPage.toString())
                parameters.append("page", page.toString())
            }
        }.body()
    }

    private fun SortOrder.toParam() = when (this) {
        CREATED_AT -> "created"
        UPDATED_AT -> "updated"
        PUSHED_AT -> "pushed"
        NAME -> "full_name"
        STARGAZERS -> throw IllegalArgumentException("Unsupported sort order for REST API: star")
    }
}
