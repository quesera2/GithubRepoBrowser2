package que.sera.sera.githubbrowser2

interface GitHubApi {

    /**
     * ユーザー情報からアバターと表示名を取得
     */
    suspend fun fetchUser(username: String): GitHubUser

    /**
     * 指定した[username]のリポジトリを最大100件取得する
     *
     * Paging3を使おうと考えたがSwiftUIでハンドリングする方法がほとんど提供されてないので一旦スルー
     */
    suspend fun fetchRepos(
        username: String,
        perPage: Int = 100,
        sort: String = "updated"
    ): List<GitHubRepo>

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
