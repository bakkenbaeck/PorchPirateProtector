//
//  DeviceDetailViewController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/19/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

class DeviceDetailViewController: UIViewController {
    
    @IBOutlet private var unlockButton: UIButton!
    @IBOutlet private var lockButton: UIButton!
    
    @IBOutlet private var errorLabel: UILabel!
    @IBOutlet private var loadingSpinner: UIActivityIndicatorView!
    
    var pairedDevice: PairedDevice!
    
    private lazy var presenter = DeviceDetailPresenter(view: self, device: self.pairedDevice, storage: Keychain.shared)
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)        
        self.presenter.getStatus()
    }
    
    @IBAction private func lockTapped() {
        self.presenter.lock()
    }
    
    @IBAction private func unlockTapped() {
        self.presenter.unlock()
    }
}

// MARK: - DeviceDetailView

extension DeviceDetailViewController: DeviceDetailView {
    func setTitle(toString: String) {
        self.title = toString
    }
    
    func setLockButtonEnabled(enabled: Bool) {
        self.lockButton.isEnabled = enabled
    }
    
    func setUnlockButtonEnabled(enabled: Bool) {
        self.unlockButton.isEnabled = enabled
    }
    
    func setApiError(toString: String?) {
        self.errorLabel.text = toString
        self.errorLabel.isHidden = (toString == nil)
    }
    
    func startLoadingIndicator() {
        self.loadingSpinner.startAnimating()
    }
    
    func stopLoadingIndicator() {
        self.loadingSpinner.stopAnimating()
    }
}
