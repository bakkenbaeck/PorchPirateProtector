//
//  BannerView.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/20/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

class BannerView: UIView {
    
    @IBOutlet private(set) var messageLabel: UILabel!
    
    var messageTextColor: UIColor = .white {
        didSet {
            self.messageLabel.textColor = self.messageTextColor
        }
    }
}

extension BannerView: NibLoadable {}
