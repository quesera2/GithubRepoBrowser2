import SwiftUI
import Shared

struct GitHubRepositoryCell: View {
    let repo: GitHubRepo
    var rank: Int?

    private var ownerName: String {
        repo.fullName.components(separatedBy: "/").first ?? ""
    }

    var body: some View {
        GroupBox {
            VStack(alignment: .leading, spacing: 8) {
                HStack(alignment: .center, spacing: 8) {
                    if let rank {
                        rankBox(rank)
                    }

                    (Text(ownerName).foregroundStyle(.themeSecondary)
                     + Text("/").foregroundStyle(.themeOutline)
                     + Text(repo.name)
                        .fontWeight(.semibold)
                        .foregroundStyle(.themeLink))
                    .font(.system(size: 16))
                    .lineLimit(1)
                }

                if let description = repo.description_ {
                    Text(description)
                        .font(.system(size: 13, weight: .regular))
                        .foregroundStyle(.secondary)
                        .lineLimit(2)
                }

                HStack(spacing: 8) {
                    if let language = repo.language {
                        metaChip {
                            Circle()
                                .foregroundStyle(Color.languageColor(language))
                                .frame(width: 8, height: 8)

                            Text(language)
                        }
                    }

                    metaChip {
                        Image(.iconStar)
                            .resizable()
                            .frame(width: 10, height: 10)
                            .foregroundStyle(.themeAccent)

                        Text(repo.stars.formatCount().localized())
                    }

                    metaChip {
                        Image(.iconFork)
                            .resizable()
                            .frame(width: 10, height: 10)
                            .foregroundStyle(.themeSecondary)

                        Text(repo.forks.formatCount().localized())
                    }
                }
                .font(.system(size: 12, weight: .regular))
                .foregroundStyle(.themeSecondary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(.themeOutlineVariant, lineWidth: 1)
        )
        .listRowInsets(EdgeInsets())
        .listRowSeparator(.hidden)
        .listRowBackground(Color.clear)
        .frame(minHeight: 44)
        .padding(.vertical, 8)
        .padding(.horizontal, 16)
        .backgroundStyle(.themeSurface)
    }

    private func rankBox(_ rank: Int) -> some View {
        let (bgColor, textColor): (Color, Color) = rank <= 3
            ? (.themeAccent, .themeOnAccent)
            : (.themeLink, .themeOnLink)

        return Text(rank.description)
            .font(.system(size: 11, weight: .medium))
            .foregroundStyle(textColor)
            .padding(.horizontal, 6)
            .padding(.vertical, 3)
            .frame(minWidth: 24, minHeight: 20)
            .background(bgColor, in: RoundedRectangle(cornerRadius: 6))
    }

    @ViewBuilder
    private func metaChip<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        HStack(spacing: 4) {
            content()
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .background(.themeSurfaceVariant, in: RoundedRectangle(cornerRadius: 6))
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