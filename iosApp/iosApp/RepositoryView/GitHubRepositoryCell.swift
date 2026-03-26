import SwiftUI
import Shared

struct GitHubRepositoryCell: View {
    let repo: GitHubRepo
    
    var body: some View {
        ZStack(alignment: .bottom) {
            VStack(alignment: .leading, spacing: 8) {
                Text(repo.name)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundStyle(Color.themeLink)

                if let description = repo.description_ {
                    Text(description)
                        .font(.system(size: 13, weight: .regular))
                        .foregroundStyle(.secondary)
                        .lineLimit(2)
                }

                HStack(spacing: 14) {
                    if let language = repo.language {
                        HStack(spacing: 4) {
                            Circle()
                                .foregroundStyle(Color.languageColor(language))
                                .frame(width: 10, height: 10)

                            Text(language)
                                .font(.system(size: 12, weight: .regular))
                                .foregroundStyle(Color.themeSecondary)
                        }
                    }

                    detailLabel(.iconStar, label: repo.stars.formatCount().localized())

                    detailLabel(.iconFork, label: repo.forks.formatCount().localized())
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
            
            Divider()
                  .overlay(.themeOutline)

        }
        .listRowInsets(EdgeInsets())
        .listRowSeparator(.hidden)
        .frame(minHeight: 44)
    }
    
    
    @ViewBuilder
    private func detailLabel(_ image: SwiftUI.ImageResource, label: String) -> some View {
        HStack(spacing: 4) {
            Image(image)
                .foregroundStyle(Color.themeAccent)
            
            Text(label)
                .font(.system(size: 12, weight: .regular))
                .foregroundStyle(Color.themeSecondary)
        }
    }
}

#Preview {
    let sampleRepos: [GitHubRepo] = [
        GitHubRepo(
            id: 1,
            name: "swift-composable-architecture",
            fullName: "pointfreeco/swift-composable-architecture",
            description: "A library for building applications in a consistent and understandable way, with composition, testing, and ergonomics in mind",
            stars: 12000,
            forks: 1300,
            language: "Swift",
            htmlUrl: "https://github.com/pointfreeco/swift-composable-architecture"
        ),
        GitHubRepo(
            id: 2,
            name: "dotfiles",
            fullName: "user/dotfiles",
            description: nil,
            stars: 5,
            forks: 0,
            language: "Shell",
            htmlUrl: "https://github.com/user/dotfiles"
        ),
        GitHubRepo(
            id: 3,
            name: "my-project",
            fullName: "user/my-project",
            description: "A personal project",
            stars: 0,
            forks: 0,
            language: nil,
            htmlUrl: "https://github.com/user/my-project"
        ),
    ]
    List(sampleRepos, id: \.id) { repo in
        GitHubRepositoryCell(repo: repo)
    }
    .listStyle(.plain)
}
