package no.bakkenbaeck.pppshared.model

data class LockState(
    val deviceId: String,
    val isLocked: Boolean
) {

    companion object {
        fun fromJSONString(json: String): LockState {
            // TODO: Actually parse
            return LockState("1234",true)
        }
    }
}