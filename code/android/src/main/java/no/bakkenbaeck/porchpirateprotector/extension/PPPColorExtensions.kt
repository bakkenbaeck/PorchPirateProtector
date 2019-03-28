package no.bakkenbaeck.porchpirateprotector.extension

import android.graphics.Color
import no.bakkenbaeck.pppshared.ui.PPPColor

fun PPPColor.toAndroidColor(): Int {
    return Color.parseColor(this.hexColor)
}