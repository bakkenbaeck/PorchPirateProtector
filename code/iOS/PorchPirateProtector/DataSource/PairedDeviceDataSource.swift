//
//  PairedDeviceDataSource.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/20/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

protocol DeviceSelectionDelegate: class {
    func didSelectDevice(_ device: PairedDevice)
}

class PairedDeviceDataSource: SingleSelectionDataSource<PairedDevice, DeviceCell> {
    
    private weak var delegate: DeviceSelectionDelegate?
    
    init(tableView: UITableView,
         devices: [PairedDevice],
         delegate: DeviceSelectionDelegate) {
        
        self.delegate = delegate
        super.init(tableView: tableView, items: devices)
    }
    
    override func configure(cell: DeviceCell, for device: PairedDevice) {
        cell.deviceNameLabel.text = device.displayName()
        cell.deviceLockStateLabel.text = device.lockStateEmoji()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        super.tableView(tableView, didSelectRowAt: indexPath)
        let device = self.item(at: indexPath)
        self.delegate?.didSelectDevice(device)
    }
}
