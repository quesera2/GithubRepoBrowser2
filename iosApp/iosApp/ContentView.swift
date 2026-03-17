import SwiftUI
import Combine
import KMPNativeCoroutinesCombine
import Shared

@Observable
final class ContentViewModel {
    var uiState: RepoUiState = RepoUiState.Idle.shared
    var query: String = ""
    var showError: Bool = false
    var errorMessage: String = ""

    private let vm: RepoViewModel
    private var cancellables = Set<AnyCancellable>()

    init() {
        vm = AppGraphCompanion.shared.invoke().repoViewModel
        createPublisher(for: vm.uiStateFlow)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { _ in },
                receiveValue: { [weak self] state in
                    self?.uiState = state
                    if let error = state as? RepoUiState.Error {
                        self?.errorMessage = error.message
                        self?.showError = true
                    }
                }
            )
            .store(in: &cancellables)
    }

    func fetchRepos() {
        guard !query.isEmpty else { return }
        vm.fetchRepos(username: query)
    }
}

struct ContentView: View {
    @State private var viewModel = ContentViewModel()
    @State private var isSearchPresented = false

    var body: some View {
        @Bindable var viewModel = viewModel

        NavigationStack {
            content
                .navigationTitle(navigationTitle)
                .searchable(
                    text: $viewModel.query,
                    isPresented: $isSearchPresented,
                    prompt: "ユーザー名を入力してください"
                )
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .onSubmit(of: .search) {
                    viewModel.fetchRepos()
                    isSearchPresented = false
                }
        }
        .alert("エラー", isPresented: $viewModel.showError) {
            Button("OK") {}
        } message: {
            Text(viewModel.errorMessage)
        }
    }

    @ViewBuilder
    private var content: some View {
        if viewModel.uiState is RepoUiState.Loading {
            ProgressView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if let success = viewModel.uiState as? RepoUiState.Success {
            if success.repos.isEmpty {
                ContentUnavailableView(
                    "リポジトリが見つかりません",
                    systemImage: "folder",
                    description: Text("このユーザーにはリポジトリがありません")
                )
            } else {
                List(success.repos, id: \.id) { repo in
                    GitHubRepositoryView(repo: repo)
                        .listRowSeparator(.visible)
                }
                .listStyle(.plain)
            }
        }
    }

    private var navigationTitle: String {
        if let success = viewModel.uiState as? RepoUiState.Success {
            return "リポジトリ一覧（\(success.repos.count)件）"
        }
        return "リポジトリ一覧"
    }
}
