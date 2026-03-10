package que.sera.sera.githubbrowser2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform