package no.bakkenbaeck.pppshared.ui

sealed class UnderlineStyle(
    val height: Int = 1,
    val color: PPPColor = PPPColor.TextDark
) {
    class None: UnderlineStyle(
        height = 0
    )
    class Active: UnderlineStyle(
        color = PPPColor.ColorPrimaryDark
    )
    class Default: UnderlineStyle()
}