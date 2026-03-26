package que.sera.sera.githubbrowser2

import dev.icerock.moko.resources.desc.RawStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.format

private const val KILO = 1000

fun Int.formatCount(): StringDesc = when {
    this < KILO -> RawStringDesc(this.toString())
    else -> {
        // 100の位で四捨五入する
        val tenths = (this + 50) / 100
        MR.strings.format_over_kilo.format(tenths / 10, tenths % 10)
    }
}