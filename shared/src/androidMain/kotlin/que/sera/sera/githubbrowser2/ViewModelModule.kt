package que.sera.sera.githubbrowser2

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ContributesTo(AppScope::class)
actual interface ViewModelModule {
    @Provides
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    fun provideHomeViewModel(viewModel: SearchViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ViewModelKey(TrendViewModel::class)
    fun provideTrendViewModel(viewModel: TrendViewModel): ViewModel = viewModel
}
