package que.sera.sera.githubbrowser2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class GitHubApiKtorImpl(
    private val httpClient: HttpClient,
) : GitHubApi {

    override suspend fun fetchUser(username: String): GitHubUser =
        httpClient.get("users/$username").body()

    override suspend fun fetchRepos(
        username: String,
        perPage: Int,
        sort: String
    ): List<GitHubRepo> =
        httpClient.get("users/$username/repos") {
            url {
                parameters.append("per_page", perPage.toString())
                parameters.append("sort", sort)
            }
        }.body()

    override suspend fun fetchTrendingRepos(
        language: String?,
        perPage: Int,
        page: Int,
    ): GitHubSearchResult {
        val since = Clock.System.todayIn(TimeZone.UTC).minus(1, DateTimeUnit.MONTH)
        val query = buildString {
            append("pushed:>$since")
            if (language != null) append("+language:$language")
        }
        return httpClient.get("search/repositories") {
            url {
                parameters.append("q", query)
                parameters.append("sort", "stars")
                parameters.append("order", "desc")
                parameters.append("per_page", perPage.toString())
                parameters.append("page", page.toString())
            }
        }.body()
    }
}
