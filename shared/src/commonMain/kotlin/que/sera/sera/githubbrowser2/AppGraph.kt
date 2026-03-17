package que.sera.sera.githubbrowser2

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface AppGraph {
    val repoViewModel: RepoViewModel
}