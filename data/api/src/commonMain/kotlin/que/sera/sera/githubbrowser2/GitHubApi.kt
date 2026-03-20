package que.sera.sera.githubbrowser2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json

class GitHubApi(
    json: Json
) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.github.com"
            }
        }
    }

    /**
     * 指定した[username]のリポジトリを最大100件取得する
     *
     * Paging3を使おうと考えたがSwiftUIでハンドリングする方法がほとんど提供されてないので一旦スルー
     */
    suspend fun fetchRepos(
        username: String,
        perPage: Int = 100,
        sort: String = "updated"
    ): List<GitHubRepo> = client.get("users/$username/repos") {
        url {
            parameters.append("per_page", perPage.toString())
            parameters.append("sort", sort)
        }
    }.body()

    /**
     * 直近1ヶ月以内に push されたリポジトリをスター数降順で返す
     *
     * [language] を指定すると言語でフィルタできる
     */
    suspend fun fetchTrendingRepos(
        language: String? = null,
        perPage: Int = 30,
        page: Int = 1,
    ): GitHubSearchResult {
        val since = Clock.System.todayIn(TimeZone.UTC).minus(1, DateTimeUnit.MONTH)
        val query = buildString {
            append("pushed:>$since")
            if (language != null) append("+language:$language")
        }
        return client.get("search/repositories") {
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