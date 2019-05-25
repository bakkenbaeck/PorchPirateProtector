package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LockState(
    val deviceId: Int,
    val isLocked: Boolean
) {
    companion object {
        @kotlinx.serialization.UnstableDefault
        fun fromJSONString(string: String): LockState {
            val serializer = LockState.serializer()
            return Json.parse(serializer, string)
        }
    }

    @kotlinx.serialization.UnstableDefault
    fun toJSONString(): String {
        return Json.stringify(LockState.serializer(), this)
    }
}