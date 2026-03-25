import SwiftUI
import Shared

// MARK: - GitHubAPIClient

private struct RepoViewModelKey: EnvironmentKey {
    static var defaultValue: RepoViewModel? = nil
}

private struct TrendViewModelKey: EnvironmentKey {
    static var defaultValue: TrendViewModel? = nil
}

extension EnvironmentValues {
    var repoViewModel: RepoViewModel? {
        get { self[RepoViewModelKey.self] }
        set { self[RepoViewModelKey.self] = newValue }
    }

    var trendViewModel: TrendViewModel? {
        get { self[TrendViewModelKey.self] }
        set { self[TrendViewModelKey.self] = newValue }
    }
}
