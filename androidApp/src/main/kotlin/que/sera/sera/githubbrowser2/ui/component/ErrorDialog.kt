package que.sera.sera.githubbrowser2.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.stringResource
import que.sera.sera.githubbrowser2.ErrorMessage
import que.sera.sera.githubbrowser2.MR

@Composable
fun ErrorDialog(
    errorMessage: ErrorMessage,
    onDismiss: () -> Unit,
) {
    val message = errorMessage.message.localized()
    when (errorMessage) {
        is ErrorMessage.CanRetry -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(MR.strings.error_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = errorMessage.retryAction) { Text(stringResource(MR.strings.retry_button)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(MR.strings.close_button)) }
            },
        )
        is ErrorMessage.CancelOnly -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(MR.strings.error_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(MR.strings.close_button)) }
            },
        )
    }
}
