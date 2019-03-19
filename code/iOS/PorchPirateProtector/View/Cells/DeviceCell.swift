//
//  DeviceCell.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/19/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

class DeviceCell: UITableViewCell {
    
    @IBOutlet private(set) var deviceNameLabel: UILabel!
    @IBOutlet private(set) var deviceLockStateLabel: UILabel!
    
    override func prepareForReuse() {
        super.prepareForReuse()
        self.deviceNameLabel.text = nil
        self.deviceLockStateLabel.text = nil
    }
}
