import SwiftUI
import Shared

extension Color {
    static func languageColor(_ language: String?) -> Color {
        let argb = LanguageColorKt.languageColor(language: language)
        let a = Double((argb >> 24) & 0xFF) / 255
        let r = Double((argb >> 16) & 0xFF) / 255
        let g = Double((argb >> 8) & 0xFF) / 255
        let b = Double(argb & 0xFF) / 255
        return Color(red: r, green: g, blue: b, opacity: a)
    }
}