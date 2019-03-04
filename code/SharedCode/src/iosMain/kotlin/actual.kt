package no.bakkenbaeck.kotlinnative

import platform.UIKit.UIDevice

actual fun platformName(): String {
    return UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion()
}