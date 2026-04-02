package que.sera.sera.githubbrowser2

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec

class GitHubRepositoryImplTest : DescribeSpec({
    describe("fetchUserAndRepos") {
        context("GitHubApiExceptionが発生したとき") {
            it("RepositoryExceptionにラップされる") {
                val repository = GitHubRepositoryImpl(
                    FakeGitHubApi { throw GitHubApiException() }
                )
                shouldThrow<RepositoryException> { repository.fetchUserAndRepos("testuser") }
            }
        }
    }
}) {
    companion object {
        private class FakeGitHubApi(
            private val onSearchReposFromUser: suspend (String) -> Pair<GitHubUser, List<GitHubRepo>>,
        ) : GitHubApi {
            override suspend fun searchReposFromUser(
                username: String,
                perPage: Int,
                sort: SortOrder,
            ): Pair<GitHubUser, List<GitHubRepo>> = onSearchReposFromUser(username)

            override suspend fun fetchTrendingRepos(
                language: String?,
                perPage: Int,
                page: Int,
            ) = GitHubSearchResult(0, emptyList())
        }

    }
}
