import SwiftUI
import Shared

extension StringProtocol where Self == String {
    static func stringResource(_ resource: KeyPath<MR.strings, StringResource>) -> String {
        MR.strings()[keyPath: resource].desc().localized()
    }
}

extension Text {
    init(_ resource: KeyPath<MR.strings, StringResource>) {
        self.init(LocalizedStringKey(MR.strings()[keyPath: resource].resourceId),
                  bundle: MR.strings()[keyPath: resource].bundle)
    }
}
