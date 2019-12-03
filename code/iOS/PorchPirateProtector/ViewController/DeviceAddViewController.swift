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
    
    private lazy var presenter = DeviceAddPresenter()
    
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
        let viewModel = self.presenter.initialViewModel(insecureStorage: UserDefaultsWrapper.shared)
        self.configureForViewModel(viewModel)
    }
    
    private func configureForViewModel(_ viewModel: DeviceAddPresenter.DeviceAddViewModel) {
        self.dataSource.updateItems(to: viewModel.availableIPAddresses)
        
        if viewModel.indicatorAnimating {
            self.loadingSpinner.startAnimating()
        } else {
            self.loadingSpinner.stopAnimating()
        }
        
        if let error = viewModel.errorMessage {
            self.showErrorBanner(with: error)
        } // else nothing to show
        
        if viewModel.deviceAdded {
            self.deviceAddedSuccessfully()
        }
    }
    
    private func deviceAddedSuccessfully() {
        self.navigationController?.showBanner(with: "Device added successfully!", backgroundColor: PPPColor.colorprimarydark.toUIColor())
        self.navigationController?.popViewController(animated: true)
    }
}

// MARK: - IPAddressSelectionDelegate

extension DeviceAddViewController: IPAddressSelectionDelegate {
    
    func didSelectIPAddress(_ ipAddress: String) {
        self.presenter.addDevice(
            deviceIpAddress: ipAddress,
            initialViewModelHandler: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            },
            insecureStorage: UserDefaultsWrapper.shared,
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            })
    }
}
