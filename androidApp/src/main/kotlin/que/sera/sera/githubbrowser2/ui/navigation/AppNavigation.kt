package que.sera.sera.githubbrowser2.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import que.sera.sera.githubbrowser2.R
import que.sera.sera.githubbrowser2.ui.search.RouteSearch
import que.sera.sera.githubbrowser2.ui.search.SearchScreen
import que.sera.sera.githubbrowser2.ui.trend.RouteTrend
import que.sera.sera.githubbrowser2.ui.trend.TrendScreen

private val TOP_LEVEL_ROUTES = mapOf(
    RouteTrend to NavBarItem(
        icon = R.drawable.icon_trend,
        description = que.sera.sera.githubbrowser2.feature.trending.MR.strings.trending_title.desc()
    ),
    RouteSearch to NavBarItem(
        icon = R.drawable.icon_search,
        description = que.sera.sera.githubbrowser2.feature.repoview.MR.strings.search_title.desc()
    ),
)

@Composable
fun AppNavigation() {
    val navigationState = rememberNavigationState(
        startRoute = RouteTrend,
        topLevelRoutes = TOP_LEVEL_ROUTES.keys
    )

    val navigator = remember { Navigator(navigationState) }

    val entryProvider = remember {
        entryProvider {
            entry<RouteTrend> {
                TrendScreen()
            }
            entry<RouteSearch> {
                SearchScreen()
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                TOP_LEVEL_ROUTES.forEach { (key, value) ->
                    val isSelected = key == navigationState.topLevelRoute
                    val description = value.description.localized()
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { navigator.navigate(key) },
                        icon = {
                            Icon(
                                painter = painterResource(value.icon),
                                contentDescription = description
                            )
                        },
                        label = { Text(description) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = { navigator.goBack() },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        )
    }
}