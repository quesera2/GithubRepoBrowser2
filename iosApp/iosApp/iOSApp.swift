import SwiftUI
import Shared

@main
struct iOSApp: App {

    var body: some Scene {
        let graph = AppGraphKt.createGraph()
        WindowGroup {
            TabView {
                NavigationStack {
                    TrendView()
                }
                .tabItem {
                    Label("Trending", systemImage: "chart.line.uptrend.xyaxis")
                }

                NavigationStack {
                    RepositoryView()
                }
                .tabItem {
                    Label("Search", systemImage: "magnifyingglass")
                }
            }
            .environment(\.repoViewModel, graph.repoViewModel)
            .environment(\.trendViewModel, graph.trendViewModel)
        }
    }
}
