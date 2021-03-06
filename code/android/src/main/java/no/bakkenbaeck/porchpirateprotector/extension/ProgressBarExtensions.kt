package no.bakkenbaeck.porchpirateprotector.extension

import android.view.View
import android.widget.ProgressBar

fun ProgressBar.updateAnimating(animating: Boolean) {
    if (animating) {
        showAndStartAnimating()
    } else {
        stopAnimatingAndHide()
    }
}

fun ProgressBar.showAndStartAnimating() {
    this.visibility = View.VISIBLE
    this.animate()
}

fun ProgressBar.stopAnimatingAndHide() {
    this.clearAnimation()
    this.visibility = View.GONE
}