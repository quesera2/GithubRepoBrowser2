import SwiftUI

extension Bool {
    
    func toReadOnlyBindable() -> Binding<Bool> {
        Binding(
            get: { self },
            set: { _ in }
        )
    }
}
