//
//  Identifiable.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/20/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

protocol Identifiable {
    
    static var identifier: String { get }
}

extension UITableViewCell: Identifiable {
    
    static var identifier: String {
        return String(describing: self)
    }
}
