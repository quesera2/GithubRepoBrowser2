package que.sera.sera.githubbrowser2

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface RepositoryModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideGitHubRepository(api: GitHubApi): GitHubRepository = GitHubRepositoryImpl(api)
}