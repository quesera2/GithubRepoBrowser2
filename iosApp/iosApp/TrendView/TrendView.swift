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
            .navigationTitle(Text(\.trending_title))
            .background(.themeBackground)
            .errorAlert(
                errorMessage: state.errorMessage,
                message: trendErrorMessage(state.errorMessage?.error),
                onDismissError: onDismissError
            )
    }
    
    private func trendErrorMessage(_ error: TrendViewError?) -> StringResource? {
        guard let error else { return nil }
        switch onEnum(of: error) {
        case .networkError:
            return MR.strings().network_error
        case .unknownError:
            return MR.strings().unknown_error
        }
    }
    
    @ViewBuilder
    private var content: some View {
        ZStack {
            repoListContent
            if state.isLoading {
                ProgressView()
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.themeBackground)
    }
    
    @ViewBuilder
    private var repoListContent: some View {
        if let repos = state.repos {
            if repos.isEmpty {
                VStack(spacing: 0) {
                    Text(\.no_trending_repositories_found)
                        .font(.system(size: 15, weight: .regular))
                        .foregroundStyle(.themeSecondary)
                }
            } else {
                List {
                    ForEach(Array(repos.enumerated()), id: \.element.id) { index, repo in
                        GitHubRepositoryCell(repo: repo, rank: index + 1)
                    }
                }
                .repoListStyle()
            }
        }
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
            errorMessage: ErrorMessageCanRetry(
                error: TrendViewError.NetworkError.shared,
                retryAction: {}
            )
        ),
        onRetry: {},
        onDismissError: {}
    )
}
