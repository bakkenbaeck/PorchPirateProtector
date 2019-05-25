//
//  SeriouslyIFiledARadarAboutThisIn2013NavigationController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 5/25/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

/// No, seriously: https://github.com/designatednerd/FileARadar#appearance-proxy-bug-proof
class SeriouslyIFiledARadarAboutThisIn2013NavigationController: UINavigationController {
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        // OK this part I still need to file a radar about - why the hell is this not the default behavior?!
        return self.topViewController?.preferredStatusBarStyle ?? .default
    }
}
