package que.sera.sera.githubbrowser2

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import que.sera.sera.githubbrowser2.SortOrder.CREATED_AT
import que.sera.sera.githubbrowser2.SortOrder.NAME
import que.sera.sera.githubbrowser2.SortOrder.PUSHED_AT
import que.sera.sera.githubbrowser2.SortOrder.STARGAZERS
import que.sera.sera.githubbrowser2.SortOrder.UPDATED_AT
import que.sera.sera.githubbrowser2.data.api.graphql.SearchReposFromUserQuery
import que.sera.sera.githubbrowser2.data.api.graphql.type.OrderDirection
import que.sera.sera.githubbrowser2.data.api.graphql.type.RepositoryOrder
import que.sera.sera.githubbrowser2.data.api.graphql.type.RepositoryOrderField
import kotlin.collections.emptyList

class GitHubApiGraphQLImpl(
    private val apolloClient: ApolloClient,
) : GitHubApi {

    override suspend fun searchReposFromUser(
        username: String,
        perPage: Int,
        sort: SortOrder
    ): Pair<GitHubUser, List<GitHubRepo>> {
        val query = SearchReposFromUserQuery(
            userName = username,
            perPage = perPage,
            sort = sort.toParam()
        )
        val data = try {
            val response = apolloClient.query(query).execute()
            response.dataOrThrow()
        } catch(e: ApolloException) {
            throw GitHubApiException(e)
        }

        // 該当するユーザーが見つからなかった場合
        // 本来はちゃんとエラーハンドリングするべきなのを省略
        val user = data.user ?: throw GitHubApiException()
        val githubUser = GitHubUser(
            name = user.name ?: "",
            login = user.login,
            avatarUrl = user.avatarUrl.toString()
        )
        val repositories = user.repositories.nodesFilterNotNull()
            ?.map(::convertRepositoryNodeToModel)
            ?: emptyList()
        return githubUser to repositories
    }

    override suspend fun fetchTrendingRepos(
        language: String?,
        perPage: Int,
        page: Int
    ): GitHubSearchResult {
        TODO("Not yet implemented")
    }

    private fun SortOrder.toParam(): RepositoryOrder = when (this) {
        CREATED_AT -> RepositoryOrderField.CREATED_AT
        UPDATED_AT -> RepositoryOrderField.UPDATED_AT
        PUSHED_AT -> RepositoryOrderField.PUSHED_AT
        NAME -> RepositoryOrderField.NAME
        STARGAZERS -> RepositoryOrderField.STARGAZERS
    }.let {
        RepositoryOrder(direction = OrderDirection.DESC, field = it)
    }

    private fun convertRepositoryNodeToModel(
        node: SearchReposFromUserQuery.Node,
    ) = with(node) {
        GitHubRepo(
            id = id,
            name = name,
            fullName = nameWithOwner,
            description = description,
            stars = stargazerCount,
            forks = forkCount,
            language = primaryLanguage?.name,
            htmlUrl = url.toString()
        )
    }
}
