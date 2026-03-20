package que.sera.sera.githubbrowser2

/**
 * [linguist](https://github.com/github-linguist/linguist) から人気の言語の色を引っ張ってきたもの
 *
 * 本来は yml を更新するような仕組みを入れるべきだがとりあえずこれで
 */
fun languageColor(language: String?): Long = when (language) {
    "Kotlin"     -> 0xFFA97BFF
    "Java"       -> 0xFFB07219
    "Swift"      -> 0xFFF05138
    "Objective-C"-> 0xFF438EFF
    "Python"     -> 0xFF3572A5
    "JavaScript" -> 0xFFF1E05A
    "TypeScript" -> 0xFF3178C6
    "Ruby"       -> 0xFF701516
    "Go"         -> 0xFF00ADD8
    "Rust"       -> 0xFFDEA584
    "C"          -> 0xFF555555
    "C++"        -> 0xFFF34B7D
    "C#"         -> 0xFF178600
    "PHP"        -> 0xFF4F5D95
    "Dart"       -> 0xFF00B4AB
    "Scala"      -> 0xFFDC322F
    "Shell"      -> 0xFF89E051
    "HTML"       -> 0xFFE34C26
    "CSS"        -> 0xFF563D7C
    "Vim Script" -> 0xFF199F4B
    else         -> 0xFF8B8B8B
}