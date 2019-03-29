//
//  CGFloat+Conversion.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/29/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

extension Int32 {
    
    var toCGFloat: CGFloat {
        guard let floatValue = CGFloat(exactly: self) else {
            fatalError("Could not convert Int32 to CGFloat")
        }
        
        return floatValue
    }
    
    var toInt: Int {
        return Int(self)
    }
}
    
