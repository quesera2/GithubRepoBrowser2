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
    var onDismissError: () -> Void
    
    @State private var searchText: String = ""
    @State private var isSearchPresented = false

    var body: some View {
        content
            .toolbar {
                ToolbarItem(placement: .principal) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(\.search_title)
                            .font(.system(size: 35, weight: .bold))
                            .foregroundStyle(Color.themePrimary)
                        Text(\.search_subtitle)
                            .font(.system(size: 15, weight: .regular))
                            .foregroundStyle(Color.themeSecondary)
                    }
                    .padding(.top, 40)
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
            }
            .toolbarRole(.editor)
            .searchable(
            text: $searchText,
                isPresented: $isSearchPresented,
                placement: .toolbar,
                prompt: .stringResource(\.search_placeholder)
            )
            .autocorrectionDisabled()
            .textInputAutocapitalization(.never)
            .onSubmit(of: .search) {
                onSearch(searchText)
                isSearchPresented = false
            }
            .alert(.stringResource(\.error_title), isPresented: state.isError.toReadOnlyBindable()) {
                if let e = state.errorMessage as? ErrorMessage.CanRetry {
                    Button(.stringResource(\.retry_button)) { e.retryAction() }
                }
                Button(.stringResource(\.close_button), role: .cancel) { onDismissError() }
            } message: {
                Text(verbatim: state.errorMessage?.message.localized() ?? "")
            }
    }
    
    @ViewBuilder
    private var content: some View {
        ZStack {
            repoListContent(repos: state.repos, isLoading: state.isLoading)
            
            if state.isLoading {
                ProgressView()
            }
        }
    }
    
    @ViewBuilder
    private func repoListContent(repos: [GitHubRepo]?, isLoading: Bool) -> some View {
        if let repos {
            if repos.isEmpty {
                Text(\.no_repositories_found)
                    .font(.system(size: 15, weight: .regular))
                    .foregroundStyle(.secondary)
            } else {
                List {
                    ForEach(repos, id: \.id) { repo in
                        GitHubRepositoryCell(repo: repo)
                    }
                }
                .listStyle(.plain)
                .contentMargins(.top, -36, for: .scrollContent) // SearchBar分マージンが空いてしまうためハック的な対応を入れている
            }
        } else if !isLoading {
            VStack {
                emptyView
                    .padding(.top, 60)
                Spacer()
            }
            .frame(maxWidth: .infinity)
        }
    }
    
    @ViewBuilder
    private var emptyView: some View {
        VStack(spacing: 16) {
            Image(.iconSearch)
                .renderingMode(.template)
                .resizable()
                .frame(width: 44, height: 44)
                .foregroundStyle(Color.themePlaceholder)
            
            Text(\.search_for_user)
                .font(.system(size: 15, weight: .regular))
                .foregroundStyle(Color.themePlaceholder)
        }
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
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState,
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Loading") {
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState.loading(),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Success") {
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState
                .success(repos: sampleRepos),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Success - Empty") {
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState
                .success(repos: []),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Error - CanRetry") {
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState
                .failure(errorMessage: ErrorMessage.CanRetry(
                    message: RawStringDesc(string: "ネットワークエラーが発生しました"),
                    retryAction: {}
                )),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Error - CancelOnly") {
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState
                .failure(errorMessage: ErrorMessage.CancelOnly(
                    message: RawStringDesc(string: "ユーザー名を入力してください")
                )),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}
