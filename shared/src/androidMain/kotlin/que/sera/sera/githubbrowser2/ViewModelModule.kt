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
    @ViewModelKey(RepoViewModel::class)
    fun provideHomeViewModel(viewModel: RepoViewModel): ViewModel = viewModel
}
