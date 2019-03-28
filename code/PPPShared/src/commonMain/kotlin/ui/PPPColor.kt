package no.bakkenbaeck.pppshared.ui

enum class PPPColor(val hexColor: String) {
    ColorPrimary("#05C1FD"),
    ColorPrimaryDark("#5848F4"),
    ColorAccent("#fb5502"),
    ErrorRed("#cc0000"),
    Success("#0d9b2a");

    val red: Long
        get() = hexColor.substring(1, 3).toLong(16)

    val green: Long
        get() = hexColor.substring(3, 5).toLong(16)

    val blue: Long
        get() = hexColor.substring(5, 7).toLong(16)
}