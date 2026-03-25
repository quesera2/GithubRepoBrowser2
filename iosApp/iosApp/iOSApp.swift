import SwiftUI
import Shared

@main
struct iOSApp: App {

    var body: some Scene {
        let graph = AppGraphKt.createGraph()
        WindowGroup {
            TabView {
                Tab("Trending", image: "icon_trend") {
                    NavigationStack {
                        TrendView()
                    }
                }

                Tab(role: .search) {
                    NavigationStack {
                        RepositoryView()
                    }
                }
            }
            .environment(\.repoViewModel, graph.repoViewModel)
            .environment(\.trendViewModel, graph.trendViewModel)
        }
    }
}
