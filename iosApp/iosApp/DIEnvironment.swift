import SwiftUI
import Shared

// MARK: - GitHubAPIClient

private struct SearchViewModelKey: EnvironmentKey {
    static var defaultValue: SearchViewModel? = nil
}

private struct TrendViewModelKey: EnvironmentKey {
    static var defaultValue: TrendViewModel? = nil
}

extension EnvironmentValues {
    var searchViewModel: SearchViewModel? {
        get { self[SearchViewModelKey.self] }
        set { self[SearchViewModelKey.self] = newValue }
    }

    var trendViewModel: TrendViewModel? {
        get { self[TrendViewModelKey.self] }
        set { self[TrendViewModelKey.self] = newValue }
    }
}
