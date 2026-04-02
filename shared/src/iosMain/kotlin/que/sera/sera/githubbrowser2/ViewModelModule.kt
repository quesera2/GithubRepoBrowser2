package que.sera.sera.githubbrowser2

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
actual interface ViewModelModule {
    val searchViewModel: SearchViewModel
    val trendViewModel: TrendViewModel
}