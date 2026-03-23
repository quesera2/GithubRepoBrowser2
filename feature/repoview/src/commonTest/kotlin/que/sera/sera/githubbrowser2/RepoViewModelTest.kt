package que.sera.sera.githubbrowser2

import app.cash.turbine.test
import dev.icerock.moko.resources.desc.desc
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import que.sera.sera.githubbrowser2.feature.repoview.MR

@OptIn(ExperimentalCoroutinesApi::class)
class RepoViewModelTest : DescribeSpec({
    val testDispatcher = StandardTestDispatcher()

    beforeEach {
        Dispatchers.setMain(testDispatcher)
    }

    afterEach {
        Dispatchers.resetMain()
    }

    describe("fetchRepos") {
        context("ユーザー名が空文字の場合") {
            it("CancelOnlyエラーが設定される") {
                runTest(testDispatcher.scheduler) {
                    val viewModel = RepoViewModel(FakeGitHubRepository())

                    viewModel.state.test {
                        awaitItem() // 初期状態
                        viewModel.fetchRepos("")
                        val error =
                            awaitItem().errorMessage.shouldBeInstanceOf<ErrorMessage.CancelOnly>()
                        error.message shouldBe MR.strings.please_enter_username.desc()
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        context("リポジトリ取得中の場合") {
            it("ローディング状態になる") {
                runTest(testDispatcher.scheduler) {
                    val viewModel = RepoViewModel(FakeGitHubRepository())

                    viewModel.state.test {
                        awaitItem() // 初期状態
                        viewModel.fetchRepos("quesera2")
                        val state = awaitItem()
                        state.isLoading shouldBe true
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        context("リポジトリ取得に成功した場合") {
            it("ローディング表示を行うこと") {
                runTest(testDispatcher.scheduler) {
                    val viewModel =
                        RepoViewModel(FakeGitHubRepository(Result.success(SAMPLE_REPOS)))

                    viewModel.state.test {
                        awaitItem().isLoading shouldBe false // 初期状態
                        viewModel.fetchRepos("quesera2")
                        awaitItem().isLoading shouldBe true  // ローディング中
                        awaitItem().isLoading shouldBe false // 完了後
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }

            it("リポジトリ一覧が設定される") {
                runTest(testDispatcher.scheduler) {
                    val viewModel =
                        RepoViewModel(FakeGitHubRepository(Result.success(SAMPLE_REPOS)))

                    viewModel.state.test {
                        awaitItem() // 初期状態
                        viewModel.fetchRepos("quesera2")
                        awaitItem() // ローディング中
                        val state = awaitItem()
                        state.repos shouldBe SAMPLE_REPOS
                        state.isLoading shouldBe false
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        context("リポジトリ取得に失敗した場合") {
            it("CanRetryエラーが設定される") {
                runTest(testDispatcher.scheduler) {
                    val viewModel = RepoViewModel(
                        FakeGitHubRepository(Result.failure(Exception("ネットワークエラー")))
                    )

                    viewModel.state.test {
                        awaitItem() // 初期状態
                        viewModel.fetchRepos("quesera2")
                        awaitItem() // ローディング中
                        val state = awaitItem()
                        val error = state.errorMessage.shouldBeInstanceOf<ErrorMessage.CanRetry>()
                        error.message shouldBe "ネットワークエラー".desc()
                        state.isLoading shouldBe false
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }

    describe("onErrorDismissed") {
        it("エラーメッセージがクリアされる") {
            runTest(testDispatcher.scheduler) {
                val viewModel = RepoViewModel(
                    FakeGitHubRepository(Result.failure(Exception("エラー")))
                )

                viewModel.state.test {
                    awaitItem() // 初期状態
                    viewModel.fetchRepos("quesera2")
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
        id = 1,
        name = "sample-repo",
        fullName = "user/sample-repo",
        description = "サンプル",
        stars = 100,
        forks = 10,
        language = "Kotlin",
        htmlUrl = "https://github.com/user/sample-repo",
    )
)
