//
//  NibLoadable.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/11/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

protocol NibLoadable: class {
    
    static var nibName: String { get }
    static var bundle: Bundle { get }
    static func fromNib() -> Self
}

extension NibLoadable {
    
    static var nibName: String {
        return String(describing: self)
    }
    
    static var bundle: Bundle {
        return Bundle(for: Self.self)
    }
    
    static var nib: UINib {
        return UINib(nibName: self.nibName, bundle: self.bundle)
    }
    
    static func fromNib() -> Self {
        guard
            let first = self.bundle.loadNibNamed(self.nibName, owner: nil, options: nil)?.first,
            let typed = first as? Self else {
                fatalError("Could not load \(self.nibName)")
        }
        
        return typed
    }
}
