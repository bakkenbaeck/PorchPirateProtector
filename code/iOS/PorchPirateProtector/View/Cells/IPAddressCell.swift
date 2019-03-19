//
//  IPAddressCell.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/19/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

class IPAddressCell: UITableViewCell {
    
    @IBOutlet var addressLabel: UILabel!
    
    override func prepareForReuse() {
        super.prepareForReuse()
        
        self.addressLabel.text = nil
    }
}
