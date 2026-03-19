package que.sera.sera.githubbrowser2

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import que.sera.sera.githubbrowser2.repository.RepositoryViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val application = LocalContext.current.applicationContext as AndroidApp
    val viewModelFactory = application.graph.metroViewModelFactory
    CompositionLocalProvider(LocalMetroViewModelFactory provides viewModelFactory) {
        RepositoryViewScreen()
    }
}
