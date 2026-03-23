import SwiftUI
import Shared

extension Text {
    init(_ resource: StringResource) {
        self.init(LocalizedStringKey(resource.resourceId), bundle: resource.bundle)
    }
}