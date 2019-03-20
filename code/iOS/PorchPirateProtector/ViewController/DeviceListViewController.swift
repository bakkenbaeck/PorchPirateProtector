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
    
    private lazy var presenter = DeviceListPresenter(view: self, storage: Keychain.shared)
    private lazy var dataSource = PairedDeviceDataSource(tableView: self.tableView, devices: [], delegate: self)
    
    private var selectedDevice: PairedDevice?
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.dataSource.reloadData()
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
        self.presenter.selectedAddDevice()
    }
}

extension DeviceListViewController: DeviceSelectionDelegate {
    
    func didSelectDevice(_ device: PairedDevice) {
        self.presenter.selectedDevice(device: device)
    }
}

extension DeviceListViewController: DeviceListView {
    
    func setAddButtonEnabled(enabled: Bool) {
        self.addButton.isEnabled = enabled
    }
    
    func showAddDevice() {
        self.perform(segue: DeviceListSegue.showAddDevice)
    }
    
    func deviceListUpdated(toDeviceList: [PairedDevice]) {
        self.dataSource.updateItems(to: toDeviceList)
    }
    
    func showDetailForDevice(device: PairedDevice) {
        self.selectedDevice = device
        self.perform(segue: DeviceListSegue.showDeviceDetail)
    }
    
    func apiErrorUpdated(toString: String?) {
        // TODO: Show error
    }
    
    func startLoadingIndicator() {
        self.loadingSpinner.startAnimating()
    }
    
    func stopLoadingIndicator() {
        self.loadingSpinner.stopAnimating()
    }
}
