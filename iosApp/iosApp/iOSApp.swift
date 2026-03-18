import SwiftUI
import Shared

@main
struct iOSApp: App {
    
    var body: some Scene {
        let graph = AppGraphKt.createGraph()
        WindowGroup {
            NavigationStack {
                RepositoryView()
            }
            .environment(\.repoViewModel, graph.repoViewModel)
        }
    }
}
