package que.sera.sera.githubbrowser2

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class GitHubRepo(
    @Serializable(with = LongAsStringSerializer::class) val id: String,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    @SerialName("stargazers_count") val stars: Int,
    @SerialName("forks_count") val forks: Int,
    val language: String? = null,
    @SerialName("html_url") val htmlUrl: String,
) {
    companion object {
        // GraphQLとRESTでid型が違うためのワークアラウンド
        private object LongAsStringSerializer : KSerializer<String> {
            override val descriptor = PrimitiveSerialDescriptor("LongAsString", PrimitiveKind.LONG)

            override fun serialize(
                encoder: Encoder,
                value: String
            ) = encoder.encodeLong(value.toLong())

            override fun deserialize(
                decoder: Decoder
            ): String = decoder.decodeLong().toString()
        }
    }
}
