import SwiftUI
import Shared

struct TrendView: View {
    @Environment(\.trendViewModel) private var vm: TrendViewModel?

    @State private var state: TrendViewState = TrendViewState.companion.initialState

    var body: some View {
        if let vm {
            TrendViewContent(
                state: state,
                onRetry: { vm.fetchTrending() },
                onDismissError: { vm.onErrorDismissed() }
            )
            .task {
                vm.fetchTrending()
                for await newState in vm.state {
                    state = newState
                }
            }
        }
    }
}

struct TrendViewContent: View {

    var state: TrendViewState
    var onRetry: () -> Void
    var onDismissError: () -> Void

    var body: some View {
        content
            .errorAlert(errorMessage: state.errorMessage, onDismissError: onDismissError)
    }

    @ViewBuilder
    private var content: some View {
        ZStack {
            repoListContent
            if state.isLoading {
                ProgressView()
            }
        }
    }

    @ViewBuilder
    private var repoListContent: some View {
        if let repos = state.repos {
            if repos.isEmpty {
                VStack(spacing: 0) {
                    sectionHeader
                        .padding(.horizontal)
                    Spacer()
                    Text(\.no_trending_repositories_found)
                        .font(.system(size: 15, weight: .regular))
                        .foregroundStyle(Color.themeSecondary)
                    Spacer()
                }
            } else {
                List {
                    Section {
                        ForEach(Array(repos.enumerated()), id: \.element.id) { index, repo in
                            TrendRepositoryCell(rank: index + 1, repo: repo)
                        }
                    } header: {
                        sectionHeader
                            .textCase(nil)
                            .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 8, trailing: 16))
                    }
                }
                .listStyle(.plain)
                .contentMargins(.top, 0, for: .scrollContent)
            }
        } else if !state.isLoading {
            VStack(spacing: 0) {
                sectionHeader
                    .padding(.horizontal)
                Spacer()
            }
            .frame(maxWidth: .infinity)
        }
    }

    @ViewBuilder
    private var sectionHeader: some View {
        Text(\.trending_title)
            .font(.system(size: 35, weight: .bold))
            .foregroundStyle(Color.themePrimary)
            .padding(.top, 20)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct TrendRepositoryCell: View {
    let rank: Int
    let repo: GitHubRepo

    var body: some View {
        HStack(alignment: .center, spacing: 0) {
            Text("#\(rank)")
                .font(.system(size: 16, weight: .bold))
                .foregroundStyle(Color.themeAccent)
                .frame(width: 40, alignment: .center)

            GitHubRepositoryCell(repo: repo)
        }
        .listRowInsets(EdgeInsets())
        .listRowSeparator(.hidden)
    }
}

// MARK: - Previews

private let sampleRepos: [GitHubRepo] = [
    GitHubRepo(
        id: 1,
        name: "kotlin",
        fullName: "JetBrains/kotlin",
        description: "The Kotlin Programming Language",
        stars: 50000,
        forks: 6000,
        language: "Kotlin",
        htmlUrl: "https://github.com/JetBrains/kotlin"
    ),
    GitHubRepo(
        id: 2,
        name: "swift",
        fullName: "apple/swift",
        description: "The Swift Programming Language",
        stars: 67000,
        forks: 10000,
        language: "C++",
        htmlUrl: "https://github.com/apple/swift"
    ),
    GitHubRepo(
        id: 3,
        name: "linux",
        fullName: "torvalds/linux",
        description: nil,
        stars: 180000,
        forks: 55000,
        language: "C",
        htmlUrl: "https://github.com/torvalds/linux"
    ),
]

#Preview("Loading") {
    TrendViewContent(
        state: TrendViewState.companion.initialState.loading(),
        onRetry: {},
        onDismissError: {}
    )
}

#Preview("Success") {
    TrendViewContent(
        state: TrendViewState.companion.initialState.success(repos: sampleRepos),
        onRetry: {},
        onDismissError: {}
    )
}

#Preview("Empty") {
    TrendViewContent(
        state: TrendViewState.companion.initialState.success(repos: []),
        onRetry: {},
        onDismissError: {}
    )
}

#Preview("Error") {
    TrendViewContent(
        state: TrendViewState.companion.initialState.failure(
            errorMessage: ErrorMessage.CanRetry(
                message: RawStringDesc(string: "Network error"),
                retryAction: {}
            )
        ),
        onRetry: {},
        onDismissError: {}
    )
}
