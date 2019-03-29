package no.bakkenbaeck.pppshared.ui

import platform.UIKit.UIColor

fun PPPColor.toUIColor(): UIColor {
    return UIColor(
        red = this.red.toDouble() / 255.0f,
        green = this.green.toDouble() / 255.0f,
        blue = this.blue.toDouble() / 255.0f,
        alpha = 1.0
    )
}