//
//  DeviceListViewController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/19/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

class DeviceListViewController: UIViewController {
    enum DeviceListSegue: String, Segue {
        case showAddDevice
        case showDeviceDetail
    }
    
    @IBOutlet private var tableView: UITableView!
    @IBOutlet private var addButton: UIButton!
    @IBOutlet private var loadingSpinner: UIActivityIndicatorView!
    
    private lazy var presenter = DeviceListPresenter()
    
    private lazy var dataSource = PairedDeviceDataSource(tableView: self.tableView, devices: [], delegate: self)
    
    private var selectedDevice: PairedDevice?
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.setViewControllers([self], animated: true)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(false, animated: animated)
        
        let viewModel = self.presenter.updateViewModel(
            insecureStorage: UserDefaultsWrapper.shared,
            isLoading: false,
            apiError: nil)
        self.configureForViewModel(viewModel)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let listSegue = DeviceListSegue.from(storyboardSegue: segue)
        switch listSegue {
        case .showAddDevice:
            // Nothing more to do here.
            break
        case .showDeviceDetail:
            // Assign the selected device.
            guard let detail = segue.destination as? DeviceDetailViewController else {
                assertionFailure("This is not a detail!")
                return
            }
            
            detail.pairedDevice = self.selectedDevice
            
            // Reset selected device for next selection.
            self.selectedDevice = nil
        }
    }
    
    @IBAction
    private func tappedAdd() {
        self.perform(segue: DeviceListSegue.showAddDevice)
    }
    
    private func configureForViewModel(_ viewModel: DeviceListPresenter.DeviceListViewModel) {
        self.dataSource.updateItems(to: viewModel.pairedDeviceList)
        self.setAddButtonEnabled(enabled: viewModel.addButtonEnabled)
        
        if let error = viewModel.apiError {
            self.showErrorBanner(with: error)
        } // else nothing to show
        
        if viewModel.indicatorAnimating {
            self.loadingSpinner.startAnimating()
        } else {
            self.loadingSpinner.stopAnimating()
        }
    }
    
    private func setAddButtonEnabled(enabled: Bool) {
        self.addButton.isEnabled = enabled
        self.addButton.alpha = enabled ? 1.0 : 0.5
    }
}

extension DeviceListViewController: DeviceSelectionDelegate {
    
    func didSelectDevice(_ device: PairedDevice) {
        self.showDetailForDevice(device: device)
    }
    
    private func showDetailForDevice(device: PairedDevice) {
        self.selectedDevice = device
        self.perform(segue: DeviceListSegue.showDeviceDetail)
    }
}
