package que.sera.sera.githubbrowser2.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import que.sera.sera.githubbrowser2.AndroidApp
import que.sera.sera.githubbrowser2.R
import que.sera.sera.githubbrowser2.ui.repository.RepositoryViewScreen
import que.sera.sera.githubbrowser2.ui.theme.GitHubBrowserTheme
import que.sera.sera.githubbrowser2.ui.trend.TrendScreen

private enum class Tab { Trending, Search }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val application = LocalContext.current.applicationContext as AndroidApp
    val viewModelFactory = application.graph.metroViewModelFactory
    GitHubBrowserTheme {
        CompositionLocalProvider(LocalMetroViewModelFactory provides viewModelFactory) {
            var selectedTab by remember { mutableStateOf(Tab.Trending) }
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == Tab.Trending,
                            onClick = { selectedTab = Tab.Trending },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.icon_trending),
                                    contentDescription = null,
                                )
                            },
                            label = { Text("Trending") },
                        )
                        NavigationBarItem(
                            selected = selectedTab == Tab.Search,
                            onClick = { selectedTab = Tab.Search },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.icon_search),
                                    contentDescription = null,
                                )
                            },
                            label = { Text("Search") },
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .consumeWindowInsets(innerPadding)
                ) {
                    when (selectedTab) {
                        Tab.Trending -> TrendScreen()
                        Tab.Search -> RepositoryViewScreen()
                    }
                }
            }
        }
    }
}
