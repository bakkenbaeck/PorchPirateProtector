//
//  DeviceAddViewController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/19/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

class DeviceAddViewController: UIViewController {
    
    @IBOutlet private var tableView: UITableView!
    @IBOutlet private var loadingSpinner: UIActivityIndicatorView!
    
    private lazy var presenter = DeviceAddPresenter(view: self,
                                                    storage: Keychain.shared,
                                                    insecureStorage: UserDefaultsWrapper.shared)
    
    private lazy var dataSource = IPAddressDataSource(tableView: self.tableView, addresses: [], delegate: self)
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Select Device For Pairing"
        self.dataSource.reloadData()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.presenter.updateAvailableIPAddresses()
    }
}

// MARK: - IPAddressSelectionDelegate

extension DeviceAddViewController: IPAddressSelectionDelegate {
    
    func didSelectIPAddress(_ ipAddress: String) {
        self.presenter.addDevice(deviceIpAddress: ipAddress)
    }
}

// MARK: - DeviceAddView

extension DeviceAddViewController: DeviceAddView {
    
    func updatedAvailableDeviceIPAddresses(toList: [String]) {
        self.dataSource.updateItems(to: toList)
    }
    
    func deviceAddedSuccessfully(device: PairedDevice) {
        self.navigationController?.showBanner(with: "Device added successfully!", backgroundColor: UIColor(red: 13.0 / 255.0, green: 155.0 / 255.0, blue: 42.0 / 255.0, alpha: 1))
        self.navigationController?.popViewController(animated: true)
    }
    
    func pairingErrorUpdated(toString: String?) {
        if let error = toString {
            self.showErrorBanner(with: error)
        } // else nothing to show
    }
    
    func startLoadingIndicator() {
        self.loadingSpinner.startAnimating()
    }
    
    func stopLoadingIndicator() {
        self.loadingSpinner.stopAnimating()
    }
}
