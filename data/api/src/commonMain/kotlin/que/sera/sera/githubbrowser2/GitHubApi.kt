package que.sera.sera.githubbrowser2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
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
}