//
//  AppDelegate.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/4/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        MobileDb().setupDefaultDriver()
        
        if DeviceManager().loadPairedDevicesFromDatabase().isEmpty {
            UserDefaultsWrapper.shared.storeIPAddresses(list: [
                "10.0.0.3",
                "1.7.8"
            ])
        }
        
        self.styleNavigation()
        return true
    }
    
    private func styleNavigation() {
        let navAppearance = UINavigationBar.appearance(whenContainedInInstancesOf: [SeriouslyIFiledARadarAboutThisIn2013NavigationController.self])

        let navTextColor = PPPColor.textlight.toUIColor()
        navAppearance.barTintColor = PPPColor.colorprimary.toUIColor()
        navAppearance.titleTextAttributes = [
            .foregroundColor: navTextColor
        ]
        
        let buttonAppearance = UIBarButtonItem.appearance(whenContainedInInstancesOf: [SeriouslyIFiledARadarAboutThisIn2013NavigationController.self])
        buttonAppearance.tintColor = navTextColor
    }
}

