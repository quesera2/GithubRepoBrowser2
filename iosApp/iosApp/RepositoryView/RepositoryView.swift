import SwiftUI
import Shared

struct RepositoryView: View {
    @Environment(\.repoViewModel) private var vm: RepoViewModel?
    
    @State private var state: RepoViewState = RepoViewState.companion.initialState
    
    var body: some View {
        if let vm {
            RepositoryViewContent(
                state: state,
                onSearch: { username in
                    guard !username.isEmpty else { return }
                    vm.fetchRepos(username: username)
                },
                onRetry: { username in
                    guard !username.isEmpty else { return }
                    vm.fetchRepos(username: username)
                },
                onDismissError: {
                    vm.onErrorDismissed()
                }
            )
            .task {
                for await newState in vm.state {
                    state = newState
                }
            }
        }
    }
}

struct RepositoryViewContent: View {
    
    var state: RepoViewState
    var onSearch: (String) -> Void
    var onRetry: (String) -> Void
    var onDismissError: () -> Void
    
    @State private var searchText: String = ""
    @State private var isSearchPresented = false
    
    var body: some View {
        content
            .navigationTitle(navigationTitle)
            .searchable(
                text: $searchText,
                isPresented: $isSearchPresented,
                prompt: "ユーザー名を入力してください"
            )
            .autocorrectionDisabled()
            .textInputAutocapitalization(.never)
            .onSubmit(of: .search) {
                onSearch(searchText)
                isSearchPresented = false
            }
            .alert("エラー", isPresented: state.isError.toReadOnlyBindable()) {
                Button("再試行") { onRetry(searchText) }
                Button("閉じる", role: .cancel) { onDismissError() }
            } message: {
                Text(state.errorMessage)
            }
    }
    
    @ViewBuilder
    private var content: some View {
        ZStack {
            if let repos = state.repos {
                repoListView(repos: repos)
            }
            if state.isLoading {
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
    }
    
    @ViewBuilder
    private func repoListView(repos: [GitHubRepo]) -> some View {
        if repos.isEmpty {
            ContentUnavailableView(
                "リポジトリが見つかりません",
                systemImage: "folder",
                description: Text("このユーザーにはリポジトリがありません")
            )
        } else {
            List(repos, id: \.id) { repo in
                GitHubRepositoryCell(repo: repo)
            }
            .listStyle(.plain)
        }
    }
    
    private var navigationTitle: String {
        guard
            let repos = state.repos,
            !repos.isEmpty else {
            return "リポジトリ一覧"
        }
        return "リポジトリ一覧（\(repos.count)件）"
    }
}

// MARK: - Previews

private let sampleRepos: [GitHubRepo] = [
    GitHubRepo(
        id: 1,
        name: "swift-composable-architecture",
        fullName: "pointfreeco/swift-composable-architecture",
        description: "A library for building applications in a consistent and understandable way",
        stars: 12000,
        forks: 1300,
        language: "Swift",
        htmlUrl: "https://github.com/pointfreeco/swift-composable-architecture"
    ),
    GitHubRepo(
        id: 2,
        name: "Alamofire",
        fullName: "Alamofire/Alamofire",
        description: "Elegant HTTP Networking in Swift",
        stars: 40000,
        forks: 7500,
        language: "Swift",
        htmlUrl: "https://github.com/Alamofire/Alamofire"
    ),
    GitHubRepo(
        id: 3,
        name: "dotfiles",
        fullName: "user/dotfiles",
        description: nil,
        stars: 0,
        forks: 0,
        language: nil,
        htmlUrl: "https://github.com/user/dotfiles"
    ),
]

#Preview("Idle") {
    RepositoryViewContent(
        state: RepoViewState.companion.initialState,
        onSearch: { _ in },
        onRetry: { _ in },
        onDismissError: {}
    )
}

#Preview("Loading") {
    RepositoryViewContent(
        state: RepoViewState.companion.initialState.loading(),
        onSearch: { _ in },
        onRetry: { _ in },
        onDismissError: {}
    )
}

#Preview("Success") {
    RepositoryViewContent(
        state: RepoViewState.companion.initialState
            .success(repos: sampleRepos),
        onSearch: { _ in },
        onRetry: { _ in },
        onDismissError: {}
    )
}

#Preview("Success - Empty") {
    RepositoryViewContent(
        state: RepoViewState.companion.initialState
            .success(repos: []),
        onSearch: { _ in },
        onRetry: { _ in },
        onDismissError: {}
    )
}

#Preview("Error") {
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState
                .failure(errorMessage: "ネットワークエラーが発生しました"),
            onSearch: { _ in },
            onRetry: { _ in },
            onDismissError: {}
        )
    }
}
