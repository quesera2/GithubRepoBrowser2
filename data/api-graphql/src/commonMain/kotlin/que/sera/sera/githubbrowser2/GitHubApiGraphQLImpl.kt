package que.sera.sera.githubbrowser2

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import que.sera.sera.githubbrowser2.SortOrder.CREATED_AT
import que.sera.sera.githubbrowser2.SortOrder.NAME
import que.sera.sera.githubbrowser2.SortOrder.PUSHED_AT
import que.sera.sera.githubbrowser2.SortOrder.STARGAZERS
import que.sera.sera.githubbrowser2.SortOrder.UPDATED_AT
import que.sera.sera.githubbrowser2.data.api.graphql.FetchTrendingReposQuery
import que.sera.sera.githubbrowser2.data.api.graphql.FetchTrendingReposQuery.OnRepository
import que.sera.sera.githubbrowser2.data.api.graphql.SearchReposFromUserQuery
import que.sera.sera.githubbrowser2.data.api.graphql.fragment.RepositoryFields
import que.sera.sera.githubbrowser2.data.api.graphql.type.OrderDirection
import que.sera.sera.githubbrowser2.data.api.graphql.type.RepositoryOrder
import que.sera.sera.githubbrowser2.data.api.graphql.type.RepositoryOrderField
import kotlin.time.Clock

class GitHubApiGraphQLImpl(
    private val apolloClient: ApolloClient,
) : GitHubApi {

    override suspend fun searchReposFromUser(
        username: String,
        perPage: Int,
        sort: SortOrder
    ): Pair<GitHubUser, List<GitHubRepo>> {
        val apolloQuery = SearchReposFromUserQuery(
            userName = username,
            perPage = perPage,
            sort = sort.toParam()
        )
        val data = try {
            val response = apolloClient.query(apolloQuery).execute()
            response.dataOrThrow()
        } catch (e: ApolloException) {
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
            ?.map { it.repositoryFields }
            ?.map(::convertRepositoryNodeToModel)
            ?: emptyList()
        return githubUser to repositories
    }

    override suspend fun fetchTrendingRepos(
        language: String?,
        perPage: Int,
        page: Int
    ): GitHubSearchResult {
        val since = Clock.System.todayIn(TimeZone.UTC).minus(1, DateTimeUnit.MONTH)
        val query = buildString {
            append("pushed:>$since")
            if (language != null) append(" language:$language")
            append(" sort:stars-desc")
        }
        val apolloQuery = FetchTrendingReposQuery(
            query = query,
            perPage = perPage,
            after = Optional.Absent
        )
        val data = try {
            val response = apolloClient.query(apolloQuery).execute()
            response.dataOrThrow()
        } catch (e: ApolloException) {
            throw GitHubApiException(e)
        }

        val repositories = data.search.nodesFilterNotNull()
            ?.mapNotNull { it.onRepository?.repositoryFields }
            ?.map(::convertRepositoryNodeToModel)
            ?: emptyList()

        return GitHubSearchResult(
            totalCount = data.search.repositoryCount,
            items = repositories,
        )
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
        node: RepositoryFields,
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
