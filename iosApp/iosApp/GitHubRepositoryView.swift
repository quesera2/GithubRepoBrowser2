import SwiftUI
import Shared

struct GitHubRepositoryView: View {
    let repo: GitHubRepo

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(repo.name)
                .font(.headline)

            if let description = repo.description_ {
                Text(description)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .lineLimit(2)
            }

            HStack(spacing: 16) {
                if let language = repo.language {
                    Label(language, systemImage: "chevron.left.forwardslash.chevron.right")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }

                Label("\(repo.stars)", systemImage: "star")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
        .padding(.vertical, 4)
    }
}
