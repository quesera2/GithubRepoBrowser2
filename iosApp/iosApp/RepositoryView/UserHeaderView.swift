import SwiftUI
import Shared

struct UserHeaderView: View {
    let user: GitHubUser
    
    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 12) {
                AsyncImage(url: URL(string: user.avatarUrl)) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } placeholder: {
                    Circle()
                        .foregroundStyle(.themeSurfaceVariant)
                }
                .frame(width: 44, height: 44)
                .clipShape(Circle())
                
                VStack(alignment: .leading, spacing: 2) {
                    Text(user.name)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundStyle(.themePrimary)
                    
                    Text(user.login)
                        .font(.system(size: 13, weight: .regular))
                        .foregroundStyle(.themeSecondary)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.vertical, 8)
            
            Divider()
                .padding(.vertical, 8)
        }
    }
}

#Preview {
    UserHeaderView(user: GitHubUser(
        name: "JetBrains",
        login: "jetbrains",
        avatarUrl: "https://avatars.githubusercontent.com/u/4314696"
    ))
    .padding(.horizontal, 16)
}
