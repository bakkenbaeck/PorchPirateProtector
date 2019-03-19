//
//  Segue.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/19/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

protocol Segue {
    var rawValue: String { get }
    init?(rawValue: String)
}

extension Segue {
    
    static func from(storyboardSegue: UIStoryboardSegue) -> Self {
        guard let identifier = storyboardSegue.identifier else {
            fatalError("This storyboard segue has no identifier!")
        }
        
        guard let segue = Self(rawValue: identifier) else {
            fatalError("Couldn't create a segue from identifier \(identifier)")
        }
        
        return segue
    }
}

extension UIViewController {
    
    func perform(segue: Segue, sender: Any? = nil) {
        self.performSegue(withIdentifier: segue.rawValue, sender: sender)
    }
}
