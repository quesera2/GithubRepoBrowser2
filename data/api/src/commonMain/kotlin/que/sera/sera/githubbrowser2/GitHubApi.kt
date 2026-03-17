package que.sera.sera.githubbrowser2

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class GitHubApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun fetchRepos(username: String): List<GitHubRepo> =
        client.get("https://api.github.com/users/$username/repos?per_page=100&sort=updated").body()
}