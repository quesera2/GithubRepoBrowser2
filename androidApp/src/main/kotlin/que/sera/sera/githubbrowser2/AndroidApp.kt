package que.sera.sera.githubbrowser2

import android.app.Application
import dev.zacsweers.metro.createGraph

class AndroidApp : Application() {
    val graph: AppGraph = createGraph<AppGraph>()
}
