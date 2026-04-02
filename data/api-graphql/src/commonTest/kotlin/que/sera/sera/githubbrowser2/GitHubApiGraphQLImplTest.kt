package que.sera.sera.githubbrowser2

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.testing.QueueTestNetworkTransport
import com.apollographql.apollo.testing.enqueueTestNetworkError
import com.apollographql.apollo.testing.enqueueTestResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import que.sera.sera.githubbrowser2.data.api.graphql.FetchTrendingReposQuery
import que.sera.sera.githubbrowser2.data.api.graphql.SearchReposFromUserQuery
import que.sera.sera.githubbrowser2.data.api.graphql.fragment.RepositoryFields
import que.sera.sera.githubbrowser2.data.api.graphql.type.OrderDirection
import que.sera.sera.githubbrowser2.data.api.graphql.type.RepositoryOrder
import que.sera.sera.githubbrowser2.data.api.graphql.type.RepositoryOrderField

@OptIn(ApolloExperimental::class)
class GitHubApiGraphQLImplTest : DescribeSpec({
    lateinit var apolloClient: ApolloClient
    lateinit var api: GitHubApi

    beforeEach {
        apolloClient = ApolloClient.Builder()
            .networkTransport(QueueTestNetworkTransport())
            .build()
        api = GitHubApiGraphQLImpl(apolloClient)
    }

    afterEach {
        apolloClient.close()
    }

    describe("searchReposFromUser") {
        lateinit var query: SearchReposFromUserQuery

        beforeEach {
            query = SearchReposFromUserQuery(
                userName = "test",
                perPage = 30,
                sort = RepositoryOrder(
                    direction = OrderDirection.DESC,
                    field = RepositoryOrderField.UPDATED_AT
                )
            )
        }

        context("ネットワークエラーが発生したとき") {
            it("GitHubApiExceptionにラップされる") {
                apolloClient.enqueueTestNetworkError()
                shouldThrow<GitHubApiException> {
                    api.searchReposFromUser("testuser")
                }
            }
        }

        context("ユーザーが見つからなかったとき") {
            it("GitHubApiExceptionがスローされる") {
                apolloClient.enqueueTestResponse(
                    operation = query,
                    data = SearchReposFromUserQuery.Data(user = null)
                )
                shouldThrow<GitHubApiException> {
                    api.searchReposFromUser("testuser")
                }
            }
        }

        context("正常に取得できたとき") {
            it("Pair<GitHubUser, List<GitHubRepo>>を返す") {
                apolloClient.enqueueTestResponse(
                    operation = query,
                    data = SearchReposFromUserQuery.Data(
                        user = SearchReposFromUserQuery.User(
                            name = "Test User",
                            login = "testuser",
                            avatarUrl = "https://example.com/avatar.png",
                            repositories = SearchReposFromUserQuery.Repositories(
                                nodes = listOf(
                                    SearchReposFromUserQuery.Node(
                                        __typename = "Repository",
                                        repositoryFields = FAKE_REPOSITORY_FIELDS,
                                    )
                                )
                            )
                        )
                    )
                )
                val (user, repos) = api.searchReposFromUser("testuser")
                user.login shouldBe "testuser"
                repos.first().name shouldBe "repo1"
            }
        }
    }

    describe("fetchTrendingRepos") {
        lateinit var query: FetchTrendingReposQuery

        beforeEach {
            query = FetchTrendingReposQuery(
                query = "test-query",
                perPage = 30,
            )
        }

        context("ネットワークエラーが発生したとき") {
            it("GitHubApiExceptionにラップされる") {
                apolloClient.enqueueTestNetworkError()
                shouldThrow<GitHubApiException> {
                    api.fetchTrendingRepos()
                }
            }
        }

        context("正常に取得できたとき") {
            it("GitHubSearchResultを返す") {
                apolloClient.enqueueTestResponse(
                    operation = query,
                    data = FetchTrendingReposQuery.Data(
                        search = FetchTrendingReposQuery.Search(
                            repositoryCount = 1,
                            pageInfo = FetchTrendingReposQuery.PageInfo(
                                endCursor = null,
                                hasNextPage = false,
                            ),
                            nodes = listOf(
                                FetchTrendingReposQuery.Node(
                                    __typename = "Repository",
                                    onRepository = FetchTrendingReposQuery.OnRepository(
                                        __typename = "Repository",
                                        repositoryFields = FAKE_REPOSITORY_FIELDS,
                                    )
                                )
                            )
                        )
                    )
                )
                val result = api.fetchTrendingRepos()
                result.totalCount shouldBe 1
                result.items.first().name shouldBe "repo1"
            }
        }
    }
}) {
    companion object {
        private val FAKE_REPOSITORY_FIELDS = RepositoryFields(
            id = "1",
            name = "repo1",
            nameWithOwner = "testuser/repo1",
            description = null,
            stargazerCount = 10,
            forkCount = 2,
            primaryLanguage = null,
            url = "https://github.com/testuser/repo1",
        )
    }
}
