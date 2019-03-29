package no.bakkenbaeck.pppshared.ui

import platform.UIKit.UIFont
import no.bakkenbaeck.pppshared.cgFloatValue

fun TextStyle.toUIFont(): UIFont {
    val fontSize = this.fontSize.defaultPoints.cgFloatValue
    return when (this.fontStyle) {
        FontStyle.Regular -> UIFont.systemFontOfSize(fontSize)
        FontStyle.Bold -> UIFont.boldSystemFontOfSize(fontSize)
        FontStyle.Italic -> UIFont.italicSystemFontOfSize(fontSize)
    }
}