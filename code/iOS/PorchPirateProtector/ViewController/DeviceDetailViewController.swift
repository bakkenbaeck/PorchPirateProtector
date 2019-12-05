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
            initialViewStateHandler: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            },
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            })
    }
    
    @IBAction private func lockTapped() {
        self.presenter.lock(
            initialViewStateHandler: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            },
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            })
    }
    
    @IBAction private func unlockTapped() {
        self.presenter.unlock(
            initialViewStateHandler: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            },
            secureStorage: Keychain.shared, completion: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            })
    }
    
    private func configureForViewState(_ viewState: DeviceDetailPresenter.DeviceDetailViewState) {
        self.lockButton.isEnabled = viewState.lockButtonEnabled
        self.unlockButton.isEnabled = viewState.unlockButtonEnabled

        if let apiError = viewState.errorMessage {
            self.errorLabel.text = apiError
            self.errorLabel.isHidden = false
        } else {
            self.errorLabel.isHidden = true
        }
        
        if viewState.indicatorAnimating {
            self.loadingSpinner.startAnimating()
        } else {
            self.loadingSpinner.stopAnimating()
        }
    }
}
