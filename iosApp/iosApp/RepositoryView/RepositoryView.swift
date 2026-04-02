import SwiftUI
import Shared

struct RepositoryView: View {
    @Environment(\.searchViewModel) private var vm: SearchViewModel?
    
    @State private var state: SearchViewState = SearchViewState.companion.initialState
    
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
    
    var state: SearchViewState
    var onSearch: (String) -> Void
    var onDismissError: () -> Void
    
    @State private var searchText: String = ""
    @State private var isSearchPresented = false
    
    var body: some View {
        content
            .navigationTitle(Text(\.search_title))
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
            .errorAlert(
                errorMessage: state.errorMessage,
                message: repoErrorMessage(state.errorMessage?.error),
                onDismissError: onDismissError
            )
    }
    
    private func repoErrorMessage(_ error: SearchViewError?) -> StringResource? {
        guard let error else { return nil }
        switch onEnum(of: error) {
        case .emptyUsername:
            return MR.strings().please_enter_username
        case .networkError:
            return MR.strings().network_error
        case .unknownError:
            return MR.strings().unknown_error
        }
    }
    
    @ViewBuilder
    private var content: some View {
        ZStack {
            repoListContent(user: state.user, repos: state.repos, isLoading: state.isLoading)
            
            if state.isLoading {
                ProgressView()
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.themeBackground)
    }
    
    @ViewBuilder
    private func repoListContent(user: GitHubUser?, repos: [GitHubRepo]?, isLoading: Bool) -> some View {
        if let repos, let user {
            if repos.isEmpty {
                Text(\.no_repositories_found)
                    .font(.system(size: 15, weight: .regular))
                    .foregroundStyle(.secondary)
            } else {
                List {
                    Section {
                        UserHeaderView(user: user)
                            .listRowBackground(Color.clear)
                            .listRowSeparator(.hidden)
                            .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                    }
                    Section {
                        ForEach(repos, id: \.id) { repo in
                            GitHubRepositoryCell(repo: repo)
                        }
                    }
                }
                .repoListStyle()
            }
        } else {
            VStack(spacing: 0) {
                if !isLoading {
                    emptyView
                        .padding(.top, 60)
                }
                Spacer()
            }
            .frame(maxWidth: .infinity)
        }
    }
    
    @ViewBuilder
    private var emptyView: some View {
        VStack(spacing: 16) {
            Image(.iconSearch)
                .resizable()
                .frame(width: 44, height: 44)
                .foregroundStyle(.themePlaceholder)
            
            Text(\.search_for_user)
                .font(.system(size: 15, weight: .regular))
                .foregroundStyle(.themePlaceholder)
        }
    }
    
}

// MARK: - Previews

private let sampleUser = GitHubUser(
    name: "JetBrains",
    login: "jetbrains",
    avatarUrl: "https://avatars.githubusercontent.com/u/4314696"
)

private let sampleRepos: [GitHubRepo] = [
    GitHubRepo(
        id: "1",
        name: "swift-composable-architecture",
        fullName: "pointfreeco/swift-composable-architecture",
        description: "A library for building applications in a consistent and understandable way",
        stars: 12000,
        forks: 1300,
        language: "Swift",
        htmlUrl: "https://github.com/pointfreeco/swift-composable-architecture"
    ),
    GitHubRepo(
        id: "2",
        name: "Alamofire",
        fullName: "Alamofire/Alamofire",
        description: "Elegant HTTP Networking in Swift",
        stars: 40000,
        forks: 7500,
        language: "Swift",
        htmlUrl: "https://github.com/Alamofire/Alamofire"
    ),
    GitHubRepo(
        id: "3",
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
            state: SearchViewState.companion.initialState,
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Loading") {
    NavigationStack {
        RepositoryViewContent(
            state: SearchViewState.companion.initialState.loading(),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Success") {
    NavigationStack {
        RepositoryViewContent(
            state: SearchViewState.companion.initialState
                .success(user: sampleUser, repos: sampleRepos),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Success - Empty") {
    NavigationStack {
        RepositoryViewContent(
            state: SearchViewState.companion.initialState
                .success(user: sampleUser, repos: []),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}

#Preview("Error - CanRetry") {
    NavigationStack {
        RepositoryViewContent(
            state: SearchViewState.companion.initialState
                .failure(errorMessage: ErrorMessageCanRetry(
                    error: SearchViewError.NetworkError.shared,
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
            state: SearchViewState.companion.initialState
                .failure(errorMessage: ErrorMessageCancelOnly(
                    error: SearchViewError.NetworkError.shared
                )),
            onSearch: { _ in },
            onDismissError: {}
        )
    }
}
