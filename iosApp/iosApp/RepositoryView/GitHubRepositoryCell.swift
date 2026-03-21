import SwiftUI
import Shared

struct GitHubRepositoryCell: View {
    let repo: GitHubRepo
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(repo.name)
                .font(.system(size: 16, weight: .semibold))
                .foregroundStyle(AppColor.link)
            
            if let description = repo.description_ {
                Text(description)
                    .font(.system(size: 13, weight: .regular))
                    .foregroundStyle(AppColor.secondary)
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
                            .foregroundStyle(AppColor.secondary)
                    }
                }
                
                detailLabel(.iconStar, label: "\(repo.stars)")
                
                detailLabel(.iconFork, label: "\(repo.forks)")
            }
        }
        .padding(.vertical, 4)
    }
    
    @ViewBuilder
    private func detailLabel(_ image: ImageResource, label: String) -> some View {
        HStack(spacing: 4) {
            Image(image)
                .renderingMode(.template)
                .foregroundStyle(AppColor.secondary)

            Text(label)
                .font(.system(size: 12, weight: .regular))
                .foregroundStyle(AppColor.secondary)
        }
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
