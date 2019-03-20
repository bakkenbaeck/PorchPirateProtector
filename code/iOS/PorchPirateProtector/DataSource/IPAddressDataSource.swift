//
//  IPAddressDataSource.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/20/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

protocol IPAddressSelectionDelegate: class {
    func didSelectIPAddress(_ ipAddress: String)
}

class IPAddressDataSource: SingleSelectionDataSource<String, IPAddressCell> {
    
    private weak var delegate: IPAddressSelectionDelegate?
    
    init(tableView: UITableView,
         addresses: [String],
         delegate: IPAddressSelectionDelegate) {
        self.delegate = delegate
        
        super.init(tableView: tableView, items: addresses)
    }
    
    override func configure(cell: IPAddressCell, for address: String) {
        cell.addressLabel.text = address
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let address = self.item(at: indexPath)
        self.delegate?.didSelectIPAddress(address)
    }
}
