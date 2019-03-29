package no.bakkenbaeck.pppshared.ui

sealed class ButtonStyle(
    val textStyle: TextStyle = DefaultButtonTextStyle(),
    val backgroundColor: PPPColor = PPPColor.ColorAccent,
    val selectedBackgroundColor: PPPColor = PPPColor.ColorAccentSelected,
    val minimumInnerMargin: Margin = Margin.ObjectPadding,
    val cornerRadius: CornerRadius = CornerRadius.Button
)

class DefaultButtonStyle: ButtonStyle()