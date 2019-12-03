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
    
    private lazy var presenter = DeviceDetailPresenter(device: self.pairedDevice)
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.title = self.presenter.title
        self.presenter.getStatus(
            initialViewModelHandler: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            },
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            })
    }
    
    @IBAction private func lockTapped() {
        self.presenter.lock(
            initialViewModelHandler: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            },
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            })
    }
    
    @IBAction private func unlockTapped() {
        self.presenter.unlock(
            initialViewModelHandler: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            },
            secureStorage: Keychain.shared, completion: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            })
    }
    
    private func configureForViewModel(_ viewModel: DeviceDetailPresenter.DeviceDetailViewModel) {
        self.lockButton.isEnabled = viewModel.lockButtonEnabled
        self.unlockButton.isEnabled = viewModel.unlockButtonEnabled

        if let apiError = viewModel.errorMessage {
            self.errorLabel.text = apiError
            self.errorLabel.isHidden = false
        } else {
            self.errorLabel.isHidden = true
        }
        
        if viewModel.indicatorAnimating {
            self.loadingSpinner.startAnimating()
        } else {
            self.loadingSpinner.stopAnimating()
        }
    }
}
