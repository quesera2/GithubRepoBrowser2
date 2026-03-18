import SwiftUI
import Shared

struct GitHubRepositoryCell: View {
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

#Preview {
    List {
        GitHubRepositoryCell(repo: GitHubRepo(
            id: 1,
            name: "swift-composable-architecture",
            fullName: "pointfreeco/swift-composable-architecture",
            description: "A library for building applications in a consistent and understandable way, with composition, testing, and ergonomics in mind",
            stars: 12000,
            forks: 1300,
            language: "Swift",
            htmlUrl: "https://github.com/pointfreeco/swift-composable-architecture"
        ))
        GitHubRepositoryCell(repo: GitHubRepo(
            id: 2,
            name: "dotfiles",
            fullName: "user/dotfiles",
            description: nil,
            stars: 5,
            forks: 0,
            language: "Shell",
            htmlUrl: "https://github.com/user/dotfiles"
        ))
        GitHubRepositoryCell(repo: GitHubRepo(
            id: 3,
            name: "my-project",
            fullName: "user/my-project",
            description: "A personal project",
            stars: 0,
            forks: 0,
            language: nil,
            htmlUrl: "https://github.com/user/my-project"
        ))
        GitHubRepositoryCell(repo: GitHubRepo(
            id: 4,
            name: "notes",
            fullName: "user/notes",
            description: nil,
            stars: 0,
            forks: 0,
            language: nil,
            htmlUrl: "https://github.com/user/notes"
        ))
    }
    .listStyle(.plain)
}
