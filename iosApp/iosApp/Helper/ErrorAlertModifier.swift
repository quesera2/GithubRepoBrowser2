import SwiftUI
import Shared

private struct ErrorAlertModifier<E: AnyObject>: ViewModifier {
    let errorMessage: ErrorMessage<E>?
    let message: StringResource?
    let onDismissError: () -> Void

    func body(content: Content) -> some View {
        content
            .alert(.stringResource(\.error_title), isPresented: (errorMessage != nil).toReadOnlyBindable()) {
                if let errorMessage {
                    switch onEnum(of: errorMessage) {
                    case .canRetry(let e):
                        Button(.stringResource(\.retry_button)) { e.retryAction() }
                        Button(.stringResource(\.close_button), role: .cancel) { onDismissError() }
                    case .cancelOnly:
                        Button(.stringResource(\.close_button), role: .cancel) { onDismissError() }
                    }
                }
            } message: {
                Text(verbatim: message?.desc().localized() ?? "")
            }
    }
}

extension View {
    func errorAlert<E: AnyObject>(errorMessage: ErrorMessage<E>?, message: StringResource?, onDismissError: @escaping () -> Void) -> some View {
        modifier(ErrorAlertModifier(errorMessage: errorMessage, message: message, onDismissError: onDismissError))
    }
}
