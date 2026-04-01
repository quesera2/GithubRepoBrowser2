import SwiftUI

extension View {
    func repoListStyle() -> some View {
        self
            .listStyle(.plain)
            .scrollContentBackground(.hidden)
            .contentMargins(.top, 0, for: .scrollContent)
    }
}