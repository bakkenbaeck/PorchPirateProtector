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
        let viewState = self.presenter.initialViewState(insecureStorage: UserDefaultsWrapper.shared)
        self.configureForViewState(viewState)
    }
    
    private func configureForViewState(_ viewState: DeviceAddPresenter.DeviceAddViewState) {
        self.dataSource.updateItems(to: viewState.availableIPAddresses)
        
        if viewState.indicatorAnimating {
            self.loadingSpinner.startAnimating()
        } else {
            self.loadingSpinner.stopAnimating()
        }
        
        if let error = viewState.errorMessage {
            self.showErrorBanner(with: error)
        } // else nothing to show
        
        if viewState.deviceAdded {
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
            initialViewStatelHandler: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            },
            insecureStorage: UserDefaultsWrapper.shared,
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            })
    }
}
