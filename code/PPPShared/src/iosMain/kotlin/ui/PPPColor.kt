package no.bakkenbaeck.pppshared.ui

import platform.UIKit.UIColor

fun PPPColor.toUIColor(): UIColor {
    return UIColor(
        red = this.red.toDouble(),
        green = this.green.toDouble(),
        blue = this.blue.toDouble(),
        alpha = 1.0
    )
}