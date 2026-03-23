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
            .toolbar {
                ToolbarItem(placement: .principal) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(MR.strings().search_title)
                            .font(.system(size: 35, weight: .bold))
                            .foregroundStyle(Color.themePrimary)
                        Text(MR.strings().search_subtitle)
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
                prompt: MR.strings().search_placeholder.desc().localized()
            )
            .autocorrectionDisabled()
            .textInputAutocapitalization(.never)
            .onSubmit(of: .search) {
                onSearch(searchText)
                isSearchPresented = false
            }
            .alert(MR.strings().error_title.desc().localized(), isPresented: state.isError.toReadOnlyBindable()) {
                Button(MR.strings().retry_button.desc().localized()) { onRetry(searchText) }
                Button(MR.strings().close_button.desc().localized(), role: .cancel) { onDismissError() }
            } message: {
                Text(state.errorMessage)
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
                Text(MR.strings().no_repositories_found)
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
            
            Text(MR.strings().search_for_user)
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
            onRetry: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Loading") {
    NavigationStack {
        RepositoryViewContent(
            state: RepoViewState.companion.initialState.loading(),
            onSearch: { _ in },
            onRetry: { _ in },
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
            onRetry: { _ in },
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
            onRetry: { _ in },
            onDismissError: {}
        )
    }
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
