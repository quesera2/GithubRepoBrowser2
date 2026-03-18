import SwiftUI
import Combine
import KMPNativeCoroutinesCombine
import Shared

struct RepositoryView: View {
    @Environment(\.repoViewModel) private var vm: RepoViewModel?

    var body: some View {
        if let vm {
            RepositoryViewContent(
                uiStatePublisher: createPublisher(for: vm.uiStateFlow)
                    .assertNoFailure()
                    .receive(on: DispatchQueue.main)
                    .eraseToAnyPublisher(),
                onSearch: { username in
                    guard !username.isEmpty else { return }
                    vm.fetchRepos(username: username)
                },
                onRetry: { username in
                    guard !username.isEmpty else { return }
                    vm.fetchRepos(username: username)
                }
            )
        }
    }
}

struct RepositoryViewContent: View {

    var uiStatePublisher: AnyPublisher<RepoUiState, Never>
    var onSearch: (String) -> Void
    var onRetry: (String) -> Void

    @State private var uiState: RepoUiState = RepoUiState.Idle.shared
    @State private var searchText: String = ""
    @State private var isSearchPresented = false

    private var isError: Binding<Bool> {
        Binding(
            get: { uiState is RepoUiState.Error },
            set: { _ in }
        )
    }

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
            .alert("エラー", isPresented: isError) {
                Button("OK") {
                    onRetry(searchText)
                }
            } message: {
                Text(uiState.errorMessageOrEmpty)
            }
            .onReceive(uiStatePublisher) { state in
                uiState = state
            }
    }

    @ViewBuilder
    private var content: some View {
        if uiState is RepoUiState.Loading {
            ProgressView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if let success = uiState as? RepoUiState.Success {
            if success.repos.isEmpty {
                ContentUnavailableView(
                    "リポジトリが見つかりません",
                    systemImage: "folder",
                    description: Text("このユーザーにはリポジトリがありません")
                )
            } else {
                List(success.repos, id: \.id) { repo in
                    GitHubRepositoryCell(repo: repo)
                        .listRowSeparator(.visible)
                }
                .listStyle(.plain)
            }
        }
    }

    private var navigationTitle: String {
        if let success = uiState as? RepoUiState.Success {
            return "リポジトリ一覧（\(success.repos.count)件）"
        }
        return "リポジトリ一覧"
    }
}

// MARK: Helper

extension RepoUiState {
    
    var errorMessageOrEmpty: String {
        (self as? RepoUiState.Error)?.message ?? ""
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
        uiStatePublisher: Just(RepoUiState.Idle.shared).eraseToAnyPublisher(),
        onSearch: { _ in },
        onRetry: { _ in }
    )
}

#Preview("Loading") {
    RepositoryViewContent(
        uiStatePublisher: Just(RepoUiState.Loading.shared).eraseToAnyPublisher(),
        onSearch: { _ in },
        onRetry: { _ in }
    )
}

#Preview("Success") {
    RepositoryViewContent(
        uiStatePublisher: Just(RepoUiState.Success(repos: sampleRepos) as RepoUiState).eraseToAnyPublisher(),
        onSearch: { _ in },
        onRetry: { _ in }
    )
}

#Preview("Success - Empty") {
    RepositoryViewContent(
        uiStatePublisher: Just(RepoUiState.Success(repos: []) as RepoUiState).eraseToAnyPublisher(),
        onSearch: { _ in },
        onRetry: { _ in }
    )
}

#Preview("Error") {
    NavigationStack {
        RepositoryViewContent(
            uiStatePublisher: Just(RepoUiState.Error(message: "ネットワークエラーが発生しました") as RepoUiState).eraseToAnyPublisher(),
            onSearch: { _ in },
            onRetry: { _ in }
        )
    }
}

