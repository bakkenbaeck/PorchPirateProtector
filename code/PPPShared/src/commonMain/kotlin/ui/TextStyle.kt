package no.bakkenbaeck.pppshared.ui

sealed class TextStyle(
    val fontStyle: FontStyle = FontStyle.Regular,
    val fontSize: FontSize = FontSize.GenericText,
    val fontColor: PPPColor = PPPColor.TextDark
)

class GenericTextStyle: TextStyle()

class ActiveTextStyle: TextStyle(
    fontColor = PPPColor.ColorPrimaryDark
)

class CellItemTextStyle: TextStyle(
    fontSize = FontSize.Header
)

class HeaderTextStyle: TextStyle(
    fontStyle = FontStyle.Bold,
    fontSize = FontSize.Header
)

class PlaceholderTextStyle: TextStyle(
    fontColor = PPPColor.TextMiddle
)

class ErrorTextStyle: TextStyle(
    fontColor = PPPColor.ErrorRed
)

class DefaultButtonTextStyle: TextStyle(
    fontStyle = FontStyle.Bold,
    fontColor = PPPColor.TextLight
)