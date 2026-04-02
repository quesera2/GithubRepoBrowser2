package que.sera.sera.githubbrowser2

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpHeader
import com.apollographql.apollo.network.http.HeadersInterceptor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import que.sera.sera.githubbrowser2.shared.BuildKonfig

@ContributesTo(AppScope::class)
interface DataModuleGraphQL {
    @Provides
    @SingleIn(AppScope::class)
    fun provideApolloClient(): ApolloClient = ApolloClient.Builder()
        .serverUrl("https://api.github.com/graphql")
        .addHttpInterceptor(HeadersInterceptor(
            listOf(
                HttpHeader("Authorization", "Bearer ${BuildKonfig.GITHUB_TOKEN}")
            )
        ))
        .build()

    @Provides
    @SingleIn(AppScope::class)
    fun provideGitHubApi(apolloClient: ApolloClient): GitHubApi = GitHubApiGraphQLImpl(apolloClient)
}