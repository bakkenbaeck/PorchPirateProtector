package no.bakkenbaeck.pppshared.ui

import platform.UIKit.UIFont

fun TextStyle.toUIFont(): UIFont {
    val fontSize = this.fontSize.defaultPoints.toDouble()
    return when (this.fontStyle) {
        FontStyle.Regular -> UIFont.systemFontOfSize(fontSize)
        FontStyle.Bold -> UIFont.boldSystemFontOfSize(fontSize)
        FontStyle.Italic -> UIFont.italicSystemFontOfSize(fontSize)
    }
}