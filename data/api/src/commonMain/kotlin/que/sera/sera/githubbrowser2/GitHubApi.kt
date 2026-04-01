package que.sera.sera.githubbrowser2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.serialization.JsonConvertException
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.io.IOException
import kotlin.time.Clock

class GitHubApi(
    private val httpClient: HttpClient,
) {

    /**
     * ユーザー情報からアバターと表示名を取得
     */
    suspend fun fetchUser(username: String): GitHubUser =
        runCatchingNetwork { httpClient.get("users/$username").body() }

    /**
     * 指定した[username]のリポジトリを最大100件取得する
     *
     * Paging3を使おうと考えたがSwiftUIでハンドリングする方法がほとんど提供されてないので一旦スルー
     */
    suspend fun fetchRepos(
        username: String,
        perPage: Int = 100,
        sort: String = "updated"
    ): List<GitHubRepo> = runCatchingNetwork {
        httpClient.get("users/$username/repos") {
            url {
                parameters.append("per_page", perPage.toString())
                parameters.append("sort", sort)
            }
        }.body()
    }

    /**
     * 直近1ヶ月以内に push されたリポジトリをスター数降順で返す
     *
     * [language] を指定すると言語でフィルタできる
     */
    suspend fun fetchTrendingRepos(
        language: String? = null,
        perPage: Int = 30,
        page: Int = 1,
    ): GitHubSearchResult = runCatchingNetwork {
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

    private suspend fun <T> runCatchingNetwork(block: suspend () -> T): T = try {
        block()
    } catch (e: ResponseException) {
        throw NetworkException(e)
    } catch (e: IOException) {
        throw NetworkException(e)
    } catch (e: JsonConvertException) {
        throw NetworkException(e)
    }
}
