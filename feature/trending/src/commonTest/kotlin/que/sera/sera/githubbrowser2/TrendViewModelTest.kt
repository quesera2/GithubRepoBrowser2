package que.sera.sera.githubbrowser2

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TrendViewModelTest : DescribeSpec({
    val testDispatcher = StandardTestDispatcher()

    beforeEach {
        Dispatchers.setMain(testDispatcher)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    describe("fetchTrending") {
        context("トレンド取得中の場合") {
            it("ローディング状態になる") {
                runTest(testDispatcher.scheduler) {
                    val viewModel = TrendViewModel(FakeGitHubRepository())

                    viewModel.state.test {
                        awaitItem() // 初期状態
                        viewModel.fetchTrending()
                        val state = awaitItem()
                        state.isLoading shouldBe true
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        context("トレンド取得に成功した場合") {
            it("ローディング表示を行うこと") {
                runTest(testDispatcher.scheduler) {
                    val viewModel = TrendViewModel(FakeGitHubRepository(Result.success(SAMPLE_REPOS)))

                    viewModel.state.test {
                        awaitItem().isLoading shouldBe false // 初期状態
                        viewModel.fetchTrending()
                        awaitItem().isLoading shouldBe true  // ローディング中
                        awaitItem().isLoading shouldBe false // 完了後
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }

            it("リポジトリ一覧が設定される") {
                runTest(testDispatcher.scheduler) {
                    val viewModel = TrendViewModel(FakeGitHubRepository(Result.success(SAMPLE_REPOS)))

                    viewModel.state.test {
                        awaitItem() // 初期状態
                        viewModel.fetchTrending()
                        awaitItem() // ローディング中
                        val state = awaitItem()
                        state.repos shouldBe SAMPLE_REPOS
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        context("トレンド取得に失敗した場合") {
            context("ネットワークエラーの場合") {
                it("NetworkErrorのCanRetryエラーが設定される") {
                    runTest(testDispatcher.scheduler) {
                        val viewModel = TrendViewModel(
                            FakeGitHubRepository(Result.failure(RepositoryException()))
                        )

                        viewModel.state.test {
                            awaitItem() // 初期状態
                            viewModel.fetchTrending()
                            awaitItem() // ローディング中
                            val state = awaitItem()
                            val error = state.errorMessage.shouldBeInstanceOf<ErrorMessage.CanRetry<TrendViewError>>()
                            error.error shouldBe TrendViewError.NetworkError
                            cancelAndIgnoreRemainingEvents()
                        }
                    }
                }
            }

            context("その他のエラーの場合") {
                it("UnknownErrorのCanRetryエラーが設定される") {
                    runTest(testDispatcher.scheduler) {
                        val viewModel = TrendViewModel(
                            FakeGitHubRepository(Result.failure(Exception("予期しないエラー")))
                        )

                        viewModel.state.test {
                            awaitItem() // 初期状態
                            viewModel.fetchTrending()
                            awaitItem() // ローディング中
                            val state = awaitItem()
                            val error = state.errorMessage.shouldBeInstanceOf<ErrorMessage.CanRetry<TrendViewError>>()
                            error.error shouldBe TrendViewError.UnknownError
                            cancelAndIgnoreRemainingEvents()
                        }
                    }
                }
            }
        }
    }

    describe("onErrorDismissed") {
        it("エラーメッセージがクリアされる") {
            runTest(testDispatcher.scheduler) {
                val viewModel = TrendViewModel(
                    FakeGitHubRepository(Result.failure(Exception("エラー")))
                )

                viewModel.state.test {
                    awaitItem() // 初期状態
                    viewModel.fetchTrending()
                    awaitItem() // ローディング中
                    awaitItem() // エラー状態
                    viewModel.onErrorDismissed()
                    val state = awaitItem()
                    state.isError shouldBe false
                    state.errorMessage shouldBe null
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }
})

private val SAMPLE_REPOS = listOf(
    GitHubRepo(
        id = "1",
        name = "kotlin",
        fullName = "JetBrains/kotlin",
        description = "The Kotlin Programming Language",
        stars = 50000,
        forks = 6000,
        language = "Kotlin",
        htmlUrl = "https://github.com/JetBrains/kotlin",
    )
)