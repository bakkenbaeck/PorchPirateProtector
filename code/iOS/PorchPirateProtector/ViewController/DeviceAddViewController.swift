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
    
    private lazy var presenter = DeviceAddPresenter(view: self, storage: Keychain.shared)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Select Device For Pairing"
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(false, animated: animated)

        self.presenter.updateAvailableIPAddresses()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(true, animated: animated)
        super.viewWillDisappear(animated)
    }
    
}

// MARK: - DeviceAddView

extension DeviceAddViewController: DeviceAddView {
    func updatedAvailableDeviceIPAddresses(toList: [String]) {
        // TODO: Setup table view data source
    }
    
    func deviceAddedSuccessfully(device: PairedDevice) {
        // TODO: success message
        self.navigationController?.popViewController(animated: true)
    }
    
    func pairingErrorUpdated(toString: String?) {
        // TODO: Show error
    }
    
    func startLoadingIndicator() {
        self.loadingSpinner.startAnimating()
    }
    
    func stopLoadingIndicator() {
        self.loadingSpinner.stopAnimating()
    }
}
