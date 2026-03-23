import SwiftUI
import Shared

extension StringProtocol where Self == String {
    static func stringResource(_ resource: KeyPath<MR.strings, StringResource>) -> String {
        MR.strings()[keyPath: resource].desc().localized()
    }
}

extension Text {
    init(_ resource: KeyPath<MR.strings, StringResource>) {
        let r = MR.strings()[keyPath: resource]
        self.init(LocalizedStringKey(r.resourceId), bundle: r.bundle)
    }
}
