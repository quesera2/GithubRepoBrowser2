package que.sera.sera.githubbrowser2

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface ViewModelModule {
    @Provides
    fun provideRepoViewModel(repository: GitHubRepository): RepoViewModel = RepoViewModel(repository)
}