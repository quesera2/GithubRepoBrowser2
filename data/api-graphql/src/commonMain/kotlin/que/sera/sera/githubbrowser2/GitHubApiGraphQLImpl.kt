package que.sera.sera.githubbrowser2

class GitHubApiGraphQLImpl : GitHubApi {
    override suspend fun fetchUser(username: String): GitHubUser {
        TODO("Not yet implemented")
    }

    override suspend fun fetchRepos(username: String, perPage: Int, sort: String): List<GitHubRepo> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchTrendingRepos(language: String?, perPage: Int, page: Int): GitHubSearchResult {
        TODO("Not yet implemented")
    }
}
