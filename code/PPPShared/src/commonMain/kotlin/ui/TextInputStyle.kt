package no.bakkenbaeck.pppshared.ui

sealed class TextInputStyle(
    val labelTextStyle: TextStyle = GenericTextStyle(),
    val activeColor: PPPColor = PPPColor.ColorPrimaryDark,
    val placeholderTextStyle: TextStyle = PlaceholderTextStyle(),
    val textStyle: TextStyle = GenericTextStyle(),
    val errorColor: PPPColor = PPPColor.ErrorRed,
    val underlineStyle: UnderlineStyle = UnderlineStyle.Default()
)

class DefaultTextInputStyle: TextInputStyle()