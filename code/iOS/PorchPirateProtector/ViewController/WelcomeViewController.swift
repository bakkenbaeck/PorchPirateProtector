//
//  WelcomeViewController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/11/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

class WelcomeViewController: UIViewController {
    enum WelcomeSegue: String, Segue {
        case showLogin
        case showCreateAccount
        case showDeviceList
    }
    
    private lazy var presenter = WelcomePresenter()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(true, animated: animated)
        
        if self.presenter.skipWelcome(secureStorage: Keychain.shared) {
            self.perform(segue: WelcomeSegue.showDeviceList)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.navigationController?.setNavigationBarHidden(false, animated: animated)
    }
    
    deinit {
        self.presenter.onDestroy()
    }
    
    @IBAction
    private func tappedLogin() {
        self.perform(segue: WelcomeSegue.showLogin)
    }
    
    @IBAction
    private func tappedCreateAccount() {
        self.perform(segue: WelcomeSegue.showCreateAccount)
    }
}
