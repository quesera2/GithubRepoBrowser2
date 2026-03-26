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
            .navigationBarHidden(true)
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
            .errorAlert(errorMessage: state.errorMessage, onDismissError: onDismissError)
    }
    
    @ViewBuilder
    private var sectionHeader: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(\.search_title)
                .font(.system(size: 35, weight: .bold))
                .foregroundStyle(Color.themePrimary)
            Text(\.search_subtitle)
                .font(.system(size: 15, weight: .regular))
                .foregroundStyle(Color.themeSecondary)
        }
        .padding(.top, 20)
        .frame(maxWidth: .infinity, alignment: .leading)
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
                VStack(spacing: 0) {
                    sectionHeader
                        .padding(.horizontal)
                    Spacer()
                    Text(\.no_repositories_found)
                        .font(.system(size: 15, weight: .regular))
                        .foregroundStyle(.secondary)
                    Spacer()
                }
            } else {
                List {
                    Section {
                        ForEach(repos, id: \.id) { repo in
                            GitHubRepositoryCell(repo: repo)
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
        } else {
            VStack(spacing: 0) {
                sectionHeader
                    .padding(.horizontal)
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
