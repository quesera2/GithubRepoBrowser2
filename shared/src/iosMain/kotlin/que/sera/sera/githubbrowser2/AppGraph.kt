package que.sera.sera.githubbrowser2

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
actual interface AppGraph

fun createGraph(): AppGraph {
    return createGraph<AppGraph>()
}