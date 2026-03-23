package que.sera.sera.githubbrowser2

import dev.icerock.moko.resources.desc.StringDesc

/**
 * エラーの種別を示すモデル
 *
 * 本来は :feature:common 的なモジュールに配置する
*/
sealed class ErrorMessage(
    val message: StringDesc
) {
    class CancelOnly(message: StringDesc) : ErrorMessage(message)

    class CanRetry(
        message: StringDesc,
        val retryAction: () -> Unit,
    ) : ErrorMessage(message)
}