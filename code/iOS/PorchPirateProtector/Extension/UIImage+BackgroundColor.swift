//
//  UIImage+BackgroundColor.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/29/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

extension UIImage {
    
    static func pixelFrom(color: UIColor) -> UIImage {
        let size = CGSize(width: 1, height: 1)
        let rect = CGRect(origin: .zero, size: size)

        UIGraphicsBeginImageContext(size)
        guard let context = UIGraphicsGetCurrentContext() else {
            fatalError("Could not access graphics context!")
        }
        context.setFillColor(color.cgColor)
        context.fill(rect)
        
        defer {
            UIGraphicsEndImageContext()
        }
        
        guard let image = UIGraphicsGetImageFromCurrentImageContext() else {
            fatalError("Couldn't get image from context!")
        }
        
        return image        
    }
}
