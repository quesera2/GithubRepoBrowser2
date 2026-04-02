package que.sera.sera.githubbrowser2

interface GitHubApi {

    /**
     * 指定した[username]のユーザー情報からアバターと表示名、リポジトリを[perPage]件取得する
     *
     * Paging3を使おうと考えたがSwiftUIでハンドリングする方法がほとんど提供されてないので一旦スルー
     */
    suspend fun searchReposFromUser(
        username: String,
        perPage: Int = 100,
        sort: SortOrder = SortOrder.UPDATED_AT
    ): Pair<GitHubUser, List<GitHubRepo>>

    /**
     * 直近1ヶ月以内に push されたリポジトリをスター数降順で返す
     *
     * [language] を指定すると言語でフィルタできる
     */
    suspend fun fetchTrendingRepos(
        language: String? = null,
        perPage: Int = 30,
        page: Int = 1
    ): GitHubSearchResult
}
