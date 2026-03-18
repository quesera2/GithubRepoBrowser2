import SwiftUI
import Shared

// MARK: - GitHubAPIClient

private struct RepoViewModelKey: EnvironmentKey {
    static var defaultValue: RepoViewModel? = nil
}

extension EnvironmentValues {
    var repoViewModel: RepoViewModel? {
        get { self[RepoViewModelKey.self] }
        set { self[RepoViewModelKey.self] = newValue }
    }
}
