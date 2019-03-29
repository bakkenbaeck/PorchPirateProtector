package no.bakkenbaeck.pppshared

import platform.CoreGraphics.CGFloat

val Int.cgFloatValue: CGFloat
    get() = this.toDouble()