package que.sera.sera.githubbrowser2

import dev.icerock.moko.resources.desc.StringDesc

sealed class ErrorMessage(
    val message: StringDesc
) {
    class CancelOnly(message: StringDesc) : ErrorMessage(message)

    class CanRetry(
        message: StringDesc,
        retryAction: () -> Unit,
    ) : ErrorMessage(message)
}