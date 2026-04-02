package que.sera.sera.githubbrowser2

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpHeader
import com.apollographql.apollo.network.http.HeadersInterceptor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import que.sera.sera.githubbrowser2.shared.BuildKonfig

//@ContributesTo(AppScope::class)
interface DataModuleREST {
    @Provides
    @SingleIn(AppScope::class)
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @SingleIn(AppScope::class)
    fun provideHttpClient(json: Json): HttpClient = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) { json(json) }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.github.com"
            }
        }
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideApolloClient(): ApolloClient = ApolloClient.Builder()
        .serverUrl("https://api.github.com/graphql")
        .addHttpInterceptor(
            HeadersInterceptor(
                listOf(
                    HttpHeader("Authorization", "Bearer ${BuildKonfig.GITHUB_TOKEN}")
                )
            )
        )
        .build()

    @Provides
    @SingleIn(AppScope::class)
    fun provideGitHubApi(httpClient: HttpClient): GitHubApi = GitHubApiKtorImpl(httpClient)
}