import SwiftUI
import Combine
import KMPNativeCoroutinesCombine
import Shared

@Observable
final class RepoViewModelWrapper {

    private var vm: RepoViewModel
    
    var uiState: RepoUiState = RepoUiState.Idle.shared
    var errorMessage: String = ""
  
    private var cancellables = Set<AnyCancellable>()

    init(_ viewModel: RepoViewModel){
        vm = viewModel
        createPublisher(for: vm.uiStateFlow)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { _ in },
                receiveValue: { [weak self] state in
                    self?.uiState = state
                    if let error = state as? RepoUiState.Error {
                        self?.errorMessage = error.message
                    }
                }
            )
            .store(in: &cancellables)
    }

    func fetchRepos(username: String) {
        guard !username.isEmpty else { return }
        vm.fetchRepos(username: username)
    }
}

struct RepositoryView : View {
    @Environment(\.repoViewModel) private var vm: RepoViewModel?
    
    @State private var wrapper: RepoViewModelWrapper?

    var body: some View {
        Group {
            if let wrapper {
                RepositoryViewContent(
                    uiState: wrapper.uiState,
                    errorMessage: wrapper.errorMessage,
                    onSearch: { wrapper.fetchRepos(username: $0) }
                )
            }
        }
        .task {
            if let vm, wrapper == nil {
                wrapper = RepoViewModelWrapper(vm)
            }
        }
    }
}

struct RepositoryViewContent: View {
    
    var uiState: RepoUiState
    var errorMessage: String
    var onSearch: (String) -> Void

    @State private var searchText: String = ""
    @State private var isSearchPresented = false
    @State private var showError: Bool = false

    var body: some View {

        NavigationStack {
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
        }
        .alert("エラー", isPresented: $showError) {
            Button("OK") {}
        } message: {
            Text(errorMessage)
        }
        .onChange(of: errorMessage) { _, newValue in
            showError = !newValue.isEmpty
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
        uiState: RepoUiState.Idle.shared,
        errorMessage: "",
        onSearch: { _ in }
    )
}

#Preview("Loading") {
    RepositoryViewContent(
        uiState: RepoUiState.Loading.shared,
        errorMessage: "",
        onSearch: { _ in }
    )
}

#Preview("Success") {
    RepositoryViewContent(
        uiState: RepoUiState.Success(repos: sampleRepos),
        errorMessage: "",
        onSearch: { _ in }
    )
}

#Preview("Success - Empty") {
    RepositoryViewContent(
        uiState: RepoUiState.Success(repos: []),
        errorMessage: "",
        onSearch: { _ in }
    )
}

#Preview("Error") {
    RepositoryViewContent(
        uiState: RepoUiState.Error(message: "ネットワークエラーが発生しました"),
        errorMessage: "ネットワークエラーが発生しました",
        onSearch: { _ in }
    )
}

